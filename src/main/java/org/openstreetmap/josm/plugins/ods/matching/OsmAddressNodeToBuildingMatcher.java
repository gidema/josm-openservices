package org.openstreetmap.josm.plugins.ods.matching;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;

/**
 * <p>Try to find a matching building for every OdAddressNode passed to the OdAddressNode
 * consumer. The geometry of the OdAddressNode will be used to do the matching</p>
 * <p>If a match is found, the building parameter of the addressNode will be set to the related address
 * and the addressNode will be added to the related addresses list of the building.</p>
 * <p>If no matching building was found, The unmatched addressNode will
 * be forwarded to the unmatchedAddressNodeConsumer if available;
 *
 * @author gertjan
 *
 */
public class OsmAddressNodeToBuildingMatcher {
    private final OdsModule module;
    private Consumer<OsmAddressNode> unmatchedAddressNodeHandler;

    public OsmAddressNodeToBuildingMatcher(OdsModule module) {
        super();
        this.module = module;
    }

    public void setUnmatchedAddressNodeHandler(
            Consumer<OsmAddressNode> unmatchedAddressNodeHandler) {
        this.unmatchedAddressNodeHandler = unmatchedAddressNodeHandler;
    }

    /**
     * Find a matching building for an address.
     * TODO use the geometry index to find the building
     *
     * @param addressNode
     */
    public void match(OsmAddressNode addressNode) {
        OsmBuildingStore buildingStore = (OsmBuildingStore)module
                .getOsmLayerManager().getEntityStore(OsmBuilding.class);
        GeoIndex<OsmBuilding> geoIndex = buildingStore.getGeoIndex();
        if (addressNode.getBuilding() == null) {
            List<OsmBuilding> buildings = geoIndex.intersection(addressNode.getGeometry());
            if (buildings.size() == 0) {
                //                reportUnmatched(addressNode);
                return;
            }
            if (buildings.size() == 1) {
                OsmBuilding building = buildings.get(0);
                addressNode.setBuilding(building);
                building.getAddressNodes().add(addressNode);
                return;
            }
            List<OsmBuilding> bagBuildings = new LinkedList<>();
            List<OsmBuilding> otherBuildings = new LinkedList<>();
            for (OsmBuilding building : buildings) {
                if (building.getReferenceId() != null) {
                    bagBuildings.add(building);
                }
                else {
                    otherBuildings.add(building);
                }
            }
            if (bagBuildings.size() == 1) {
                OsmBuilding building = bagBuildings.get(0);
                addressNode.setBuilding(building);
                building.getAddressNodes().add(addressNode);
                return;
            }
            // TODO report duplicateBuildings
        }
    }

    //    /**
    //     * Find a matching building for an address.
    //     * Iterate over buildings to find the building
    //     *
    //     * @param addressNode
    //     */
    //    public void match(OdAddressNode addressNode) {
    //        OsmBuildingStore buildings = (OsmBuildingStore)module
    //                .getOsmLayerManager().getEntityStore(OdBuilding.class);
    //        if (addressNode.getBuilding() == null) {
    //            Iterator<OdBuilding> iterator = buildings.iterator();
    //            boolean found = false;
    //            while (iterator.hasNext() && !found) {
    //                OdBuilding building = iterator.next();
    //                if (building.getGeometry().covers(addressNode.getGeometry())) {
    //                    addressNode.setBuilding(building);
    //                    building.getAddressNodes().add(addressNode);
    //                    found = true;
    //                }
    //            }
    //            if (!found) {
    //                reportUnmatched(addressNode);
    //            }
    //        }
    //    }

    private void reportUnmatched(OsmAddressNode addressNode) {
        if (unmatchedAddressNodeHandler != null) {
            unmatchedAddressNodeHandler.accept(addressNode);
        }
    }
}
