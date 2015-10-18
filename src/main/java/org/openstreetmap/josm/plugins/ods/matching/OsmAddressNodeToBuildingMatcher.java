package org.openstreetmap.josm.plugins.ods.matching;

import java.util.Iterator;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.osm.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.managers.DataManager;


/**
 * <p>Try to find a matching building for every AddressNode passed to the AddressNode
 * consumer. The geometry of the AddressNode will be used to do the matching</p>
 * <p>If a match is found, the building parameter of the addressNode will be set to the related address
 * and the addressNode will be added to the related addresses list of the building.</p>
 * <p>If no matching building was found, The unmatched addressNode will
 * be forwarded to the unmatchedAddressNodeConsumer if available;
 * 
 * @author gertjan
 *
 */
public class OsmAddressNodeToBuildingMatcher {
    private DataManager dataManager;
    private Consumer<AddressNode> unmatchedAddressNodeHandler;
    
    public OsmAddressNodeToBuildingMatcher(DataManager dataManager) {
        super();
        this.dataManager = dataManager;
    }

    public void setUnmatchedAddressNodeHandler(
            Consumer<AddressNode> unmatchedAddressNodeHandler) {
        this.unmatchedAddressNodeHandler = unmatchedAddressNodeHandler;
    }

    public Consumer<AddressNode> getAddressNodeConsumer() {
        return new Consumer<AddressNode>() {

            @Override
            public void accept(AddressNode addressNode) {
                matchAddressToBuilding(addressNode);
            }
        };
    }

    /**
     * Find a matching building for an address.
     * 
     * @param addressNode
     */
    private void matchAddressToBuilding(AddressNode addressNode) {
        OsmBuildingStore buildings = dataManager.getBuildingManager().getOsmBuildings();
        if (addressNode.getBuilding() == null) {
            Iterator<Building> iterator = buildings.iterator();
            boolean found = false;
            while (iterator.hasNext() && !found) {
                Building building = iterator.next();
                if (building.getGeometry().covers(addressNode.getGeometry())) {
                    addressNode.setBuilding(building);
                    building.getAddressNodes().add(addressNode);
                    found = true;
                }
            }
            if (!found) {
                reportUnmatched(addressNode);
            }
        }
    }
    
    private void reportUnmatched(AddressNode addressNode) {
        if (unmatchedAddressNodeHandler != null) {
            unmatchedAddressNodeHandler.accept(addressNode);
        }
    }
}
