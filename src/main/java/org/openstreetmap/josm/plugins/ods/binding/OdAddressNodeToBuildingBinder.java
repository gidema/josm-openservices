package org.openstreetmap.josm.plugins.ods.binding;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuildingUnit;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdAddressNodeStore;
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

    public OdAddressNodeToBuildingBinder(OdAddressNodeStore addressNodeStore) {
        super();
        this.addressNodeStore = addressNodeStore;
    }

    @Override
    public void run() {
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
            if (buildingUnit != null) {
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
    }

    private static void bindAddressNodeToBuilding(OdAddressNode addressNode, OdBuilding building) {
        addressNode.setBuilding(building);
        building.getAddressNodes().add(addressNode);
        //        if (building.getStatus() == BuildingStatus.UNDER_CONSTRUCTION) {
        //            addressNode.setStatus(EntityStatus.UNDER_CONSTRUCTION);
        //        }
    }
}
