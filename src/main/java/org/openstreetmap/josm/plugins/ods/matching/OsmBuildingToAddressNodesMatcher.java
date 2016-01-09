package org.openstreetmap.josm.plugins.ods.matching;

import java.util.List;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.osm.OsmAddressNodeStore;

/**
 * <p>Try to find a matching address nodes for every Building. 
 * The geometry of the AddressNode will be used to do the matching</p>
 * <p>If a match is found, the building parameter of the addressNode will be set to the related address
 * and the addressNode will be added to the related addresses list of the building.</p>
 * <p>If no matching building was found, The unmatched addressNode will
 * be forwarded to the unmatchedAddressNodeConsumer if available;
 * 
 * @author gertjan
 *
 */
public class OsmBuildingToAddressNodesMatcher {
    private OdsModule module;
    private Consumer<AddressNode> unmatchedAddressNodeHandler;
    
    public OsmBuildingToAddressNodesMatcher(OdsModule module) {
        super();
        this.module = module;
    }

    public void setUnmatchedAddressNodeHandler(
            Consumer<AddressNode> unmatchedAddressNodeHandler) {
        this.unmatchedAddressNodeHandler = unmatchedAddressNodeHandler;
    }

    /**
     * Find all matching addresses for a building.
     * 
     * @param building
     */
    public void match(Building building) {
        OsmAddressNodeStore addressNodeStore = (OsmAddressNodeStore)module
                .getOsmLayerManager().getEntityStore(AddressNode.class);
        GeoIndex<AddressNode> geoIndex = addressNodeStore.getGeoIndex();
        if (building.getAddressNodes().size() == 0) {
            List<AddressNode> addressNodes = geoIndex.intersection(building.getGeometry());
            if (addressNodes.size() > 0) {
                for (AddressNode node : addressNodes) {
                    building.getAddressNodes().add(node);
                    node.setBuilding(building);
                }
            }
        }
    }
    
//    /**
//     * Find a matching building for an address.
//     * Iterate over buildings to find the building
//     * 
//     * @param addressNode
//     */
//    public void match(AddressNode addressNode) {
//        OsmBuildingStore buildings = (OsmBuildingStore)module
//                .getOsmLayerManager().getEntityStore(Building.class);
//        if (addressNode.getBuilding() == null) {
//            Iterator<Building> iterator = buildings.iterator();
//            boolean found = false;
//            while (iterator.hasNext() && !found) {
//                Building building = iterator.next();
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
    
    private void reportUnmatched(AddressNode addressNode) {
        if (unmatchedAddressNodeHandler != null) {
            unmatchedAddressNodeHandler.accept(addressNode);
        }
    }
}
