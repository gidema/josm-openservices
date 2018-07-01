package org.openstreetmap.josm.plugins.ods.matching;

import java.util.List;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;

/**
 * <p>Try to find a matching address nodes for every OdBuilding.
 * The geometry of the OdAddressNode will be used to do the matching</p>
 * <p>If a match is found, the building parameter of the addressNode will be set to the related address
 * and the addressNode will be added to the related addresses list of the building.</p>
 * <p>If no matching building was found, The unmatched addressNode will
 * be forwarded to the unmatchedAddressNodeConsumer if available;
 *
 * @author gertjan
 *
 */
public class OsmBuildingToAddressNodesMatcher {
    private Consumer<OsmAddressNode> unmatchedAddressNodeHandler;
    private final OsmAddressNodeStore addressNodeStore;

    public OsmBuildingToAddressNodesMatcher(OsmAddressNodeStore addressNodeStore) {
        super();
        this.addressNodeStore = addressNodeStore;
    }

    public void setUnmatchedAddressNodeHandler(
            Consumer<OsmAddressNode> unmatchedAddressNodeHandler) {
        this.unmatchedAddressNodeHandler = unmatchedAddressNodeHandler;
    }

    /**
     * Find all matching addresses for a building.
     *
     * @param building
     */
    public void match(OsmBuilding building) {
        GeoIndex<OsmAddressNode> geoIndex = addressNodeStore.getGeoIndex();
        if (building.getAddressNodes().size() == 0) {
            List<OsmAddressNode> addressNodes = geoIndex.intersection(building.getGeometry());
            if (addressNodes.size() > 0) {
                for (OsmAddressNode node : addressNodes) {
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
