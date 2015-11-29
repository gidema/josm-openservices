package org.openstreetmap.josm.plugins.ods.matching;

import java.util.List;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.opendata.OpenDataBuildingStore;


/**
 * <p>Try to find a matching building for every AddressNode passed to the AddressNode
 * consumer. The referenceId of the address node will be used to do the matching.</p>
 * <p>If a match is found, the building parameter of the addressNode will be set to the related address
 * and the addressNode will be added to the related addresses list of the building.</p>
 * <p>If the referenceId is null, or no building with this referenceId was found,
 * this must be an error in the integrity of the opendata object. The faulty addressNode will
 * be forwarded to the unmatchedAddressNode consumer if available;
 * 
 * @author gertjan
 *
 */
public class OpenDataAddressNodeToBuildingMatcher {
    private OdsModule module;
    private Consumer<AddressNode> unmatchedAddressNodeHandler;
    
    public OpenDataAddressNodeToBuildingMatcher(OdsModule module) {
        super();
        this.module = module;
    }

    public void setUnmatchedAddressNodeHandler(
            Consumer<AddressNode> unmatchedAddressNodeHandler) {
        this.unmatchedAddressNodeHandler = unmatchedAddressNodeHandler;
    }

    /**
     * Find a matching building for an address.
     * 
     * @param addressNode
     */
    public void matchAddressToBuilding(AddressNode addressNode) {
        OpenDataBuildingStore buildings = (OpenDataBuildingStore) module
                .getOpenDataLayerManager().getEntityStore(Building.class);
        if (addressNode.getBuilding() == null) {
            Object buildingRef = addressNode.getBuildingRef();
            if (buildingRef != null) {
                List<Building> matchedbuildings = buildings.getById(buildingRef);
                if (matchedbuildings.size() == 1) {
                    Building building = matchedbuildings.get(0);
                    addressNode.setBuilding(building);
                    building.getAddressNodes().add(addressNode);
                }
                else {
                    reportUnmatched(addressNode);
                }
            }
            else {
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
