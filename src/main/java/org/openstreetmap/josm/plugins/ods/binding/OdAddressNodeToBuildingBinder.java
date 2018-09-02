package org.openstreetmap.josm.plugins.ods.binding;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuildingUnit;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.relations.AddressNodeToBuildingRelation;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;
import org.openstreetmap.josm.tools.Logging;


/**
 * <p>Try to find a related building for every OdAddressNode passed to the OdAddressNode
 * consumer. The buildinUnit will be used to do detect the relation.</p>
 * <p>If a match is found, the building parameter of the addressNode will be set to the related address
 * and the addressNode will be added to the related addresses list of the building.</p>
 *
 * @author gertjan
 *
 */
public class OdAddressNodeToBuildingBinder implements Runnable {
    private final OdAddressNodeStore addressNodeStore;
    private final OdBuildingStore buildingStore;
    private final AddressNodeToBuildingRelation addressNodetoBuildingRelation;

    public OdAddressNodeToBuildingBinder(OdAddressNodeStore addressNodeStore,
            OdBuildingStore buildingStore,
            AddressNodeToBuildingRelation addressNodetoBuildingRelation) {
        super();
        this.addressNodeStore = addressNodeStore;
        this.buildingStore = buildingStore;
        this.addressNodetoBuildingRelation = addressNodetoBuildingRelation;
    }

    @Override
    public void run() {
        bindUsingRelation();
        bindUsingBuildingUnits();
    }

    private void bindUsingRelation() {
        addressNodetoBuildingRelation.forEach(tuple -> {
            OdAddressNode addressNode = addressNodeStore.get(tuple.getAddressNodeId());
            if (addressNode == null) {
                Logging.warn("Reference to unknown address node with id {0, number, 0000000000000000} found. The address node will be ignored", tuple.getAddressNodeId());
                return;
            }
            OdBuilding building = buildingStore.get(tuple.getBuildingId());
            if (building == null) {
                Logging.warn("Reference to unknown building with id {0, number, 0000000000000000} found. The address node will be ignored", tuple.getBuildingId());
                return;
            }
            bindAddressNodeToBuilding(addressNode, building);
        });
    }

    private void bindUsingBuildingUnits() {
        // Now try to bind Address nodes to buildings using the building unit
        for(OdAddressNode addressNode : addressNodeStore) {
            bindAddressToBuilding(addressNode);
        }
    }

    /**
     * Find a matching building for an address.
     *
     * @param addressNode
     */
    public static void bindAddressToBuilding(OdAddressNode addressNode) {
        if (addressNode.getBuilding() == null) {
            OdBuildingUnit buildingUnit = addressNode.getBuildingUnit();
            assert buildingUnit != null;
            ZeroOneMany<OdBuilding> buildings = buildingUnit.getBuildings();
            assert buildings.getCardinality() != ZeroOneMany.Cardinality.Zero;
            switch (buildings.getCardinality()) {
            case One: {
                OdBuilding building = buildings.getOne();
                bindAddressNodeToBuilding(addressNode, building);
            }
            break;
            case Many:
                for (OdBuilding building : buildings.getMany()) {
                    if (building.getGeometry().covers(addressNode.getGeometry())) {
                        bindAddressNodeToBuilding(addressNode, building);
                        break;
                    }
                }
                if (addressNode.getBuilding() == null) {
                    Logging.warn("BAG building unit {0} is not inside a building.", buildingUnit.getBuildingUnitId());
                }
                break;
            default:
                throw new UnsupportedOperationException();
            }
        }
    }

    private static void bindAddressNodeToBuilding(OdAddressNode addressNode, OdBuilding building) {
        addressNode.setBuilding(building);
        building.getAddressNodes().add(addressNode);
        if (building.getStatus() == EntityStatus.CONSTRUCTION) {
            addressNode.setStatus(EntityStatus.CONSTRUCTION);
        }
    }
}
