package org.openstreetmap.josm.plugins.ods.binding;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;

/**
 * <p>Try to find a related building for every OsmAddressNode in the store.
 * The geometries of the address node and the buildings will be used to do the matching</p>
 * <p>If a match is found, the building parameter of the addressNode will be set to the related address
 * and the addressNode will be added to the related addresses list of the building.</p>
 * <p>If no matching building was found, The unmatched addressNode will
 * be forwarded to the unmatchedAddressNodeConsumer if available;
 *
 * @author gertjan
 *
 */
public class OsmAddressNodeToBuildingBinder implements Runnable {
    private final OsmBuildingStore osmBuildingStore;
    private final OsmAddressNodeStore osmAddressNodeStore;
    private Consumer<OsmAddressNode> unmatchedAddressNodeHandler;

    public OsmAddressNodeToBuildingBinder(OsmBuildingStore osmBuildingStore, OsmAddressNodeStore osmAddressNodeStore) {
        super();
        this.osmBuildingStore = osmBuildingStore;
        this.osmAddressNodeStore = osmAddressNodeStore;
    }

    public void setUnmatchedAddressNodeHandler(
            Consumer<OsmAddressNode> unmatchedAddressNodeHandler) {
        this.unmatchedAddressNodeHandler = unmatchedAddressNodeHandler;
    }

    @Override
    public void run() {
        osmAddressNodeStore.forEach(this::bind);
    }

    /**
     * Find a matching building for an address.
     * TODO use the geometry index to find the building
     *
     * @param addressNode
     */
    public void bind(OsmAddressNode addressNode) {
        GeoIndex<OsmBuilding> geoIndex = osmBuildingStore.getGeoIndex();
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
                if (building.getBuildingId() != null) {
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
