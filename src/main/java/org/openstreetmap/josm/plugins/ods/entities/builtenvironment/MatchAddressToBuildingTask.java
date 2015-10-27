package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.Iterator;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.opendata.OpenDataAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.opendata.OpenDataBuildingStore;
import org.openstreetmap.josm.plugins.ods.tasks.Task;


/**
 * This analyzer builds the relation between address nodes and buildings
 * 
 * @author gertjan
 *
 */
public class MatchAddressToBuildingTask implements Task {
    private OpenDataBuildingStore buildingStore;
    private OpenDataAddressNodeStore addressNodeStore;
    
    public MatchAddressToBuildingTask(OpenDataBuildingStore buildingStore,
            OpenDataAddressNodeStore addressNodeStore) {
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
        List<Building> buildings = buildingStore.getById(buildingRef);
        // TODO create issue if the building is not found
        if (buildings.size() == 1) {
            address.setBuilding(buildings.get(0));
            buildings.get(0).getAddressNodes().add(address);
        }
    }

    /**
     * Use the geometry (point) of this address to find the building to
     * which this address belongs.
     * TODO Use a geometry index to do the matching 
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
