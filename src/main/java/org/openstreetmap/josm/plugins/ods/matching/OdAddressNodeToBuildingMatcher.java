package org.openstreetmap.josm.plugins.ods.matching;

import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingStore;


/**
 * <p>Try to find a matching building for every OdAddressNode passed to the OdAddressNode
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
public class OdAddressNodeToBuildingMatcher {
    private final OdBuildingStore buildingStore;
    private final OdAddressNodeStore addressNodeStore;
    //    private final OdLayerManager odLayerManager;
    private Consumer<OdAddressNode> unmatchedAddressNodeHandler;

    public OdAddressNodeToBuildingMatcher(OdBuildingStore buildingStore,
            OdAddressNodeStore addressNodeStore) {
        super();
        this.buildingStore = buildingStore;
        this.addressNodeStore = addressNodeStore;
    }

    public void setUnmatchedAddressNodeHandler(
            Consumer<OdAddressNode> unmatchedAddressNodeHandler) {
        this.unmatchedAddressNodeHandler = unmatchedAddressNodeHandler;
    }

    public void run() {
        for(OdAddressNode addressNode : addressNodeStore) {
            matchAddressToBuilding(addressNode);
        }
    }

    /**
     * Find a matching building for an address.
     *
     * @param addressNode
     */
    public void matchAddressToBuilding(OdAddressNode addressNode) {
        if (addressNode.getBuilding() == null) {
            Long buildingRef = (Long) addressNode.getBuildingRef();
            if (buildingRef != null) {
                OdBuilding building = buildingStore.get(buildingRef);
                if (building != null) {
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

    private void reportUnmatched(OdAddressNode addressNode) {
        if (unmatchedAddressNodeHandler != null) {
            unmatchedAddressNodeHandler.accept(addressNode);
        }
    }
}
