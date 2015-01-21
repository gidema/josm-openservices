package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.Iterator;

import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.tasks.Task;


/**
 * This analyzer builds the relation between address nodes and buildings
 * 
 * @author gertjan
 *
 */
public class MatchAddressToBuildingTask implements Task {
    private GtBuildingStore buildingStore;
    private GtAddressNodeStore addressNodeStore;
    
    public MatchAddressToBuildingTask(GtBuildingStore buildingStore,
            GtAddressNodeStore addressNodeStore) {
        super();
        this.buildingStore = buildingStore;
        this.addressNodeStore = addressNodeStore;
    }

    public void run(Context ctx) {
        for (AddressNode addressNode : addressNodeStore) {
            if (addressNode.getBuilding() == null) {
                Object buildingRef = addressNode.getBuildingRef();
                if (buildingRef != null) {
                    analyzeAddressBuildingByRef(addressNode);
                }
                else {
                    analyzeAddressBuildingByGeometry(addressNode);
                }
            }
        }
    }

    /**
     * Use the building reference in the address to find the building to
     * which this address belongs
     * 
     * @param address
     */
    private void analyzeAddressBuildingByRef(AddressNode address) {
        Object buildingRef = address.getBuildingRef();
        Building building = buildingStore.getByReference(buildingRef);
        // TODO create issue if the building is not found
        if (building != null) {
            address.setBuilding(building);
            building.getAddressNodes().add(address);
        }
    }

    /**
     * Use the geometry (point) of this address to find the building to
     * which this address belongs
     * 
     * @param address
     */
    private void analyzeAddressBuildingByGeometry(AddressNode address) {
        Iterator<Building> iterator = buildingStore.iterator();
        boolean found = false;
        while (iterator.hasNext() && !found) {
            Building building = iterator.next();
            if (building.getGeometry().covers(address.getGeometry())) {
                address.setBuilding(building);
                building.getAddressNodes().add(address);
                found = true;
            }
        }
    }
}
