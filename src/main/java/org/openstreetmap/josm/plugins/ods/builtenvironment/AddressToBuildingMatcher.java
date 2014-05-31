package org.openstreetmap.josm.plugins.ods.builtenvironment;

import java.util.Iterator;

import org.openstreetmap.josm.plugins.ods.DataLayer;
import org.openstreetmap.josm.plugins.ods.analysis.Analyzer;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;


/**
 * This analyzer builds the relation between address nodes and buildings
 * 
 * @author gertjan
 *
 */
public class AddressToBuildingMatcher implements Analyzer {
    
    public void analyze(DataLayer dataLayer, EntitySet newEntities) {
        BuiltEnvironment be = new BuiltEnvironment(newEntities);
        EntityStore<AddressNode> newAddresses = be.getAddresses();
        EntityStore<Building> newBuildings = be.getBuildings();
        Iterator<AddressNode> it = newAddresses.iterator();
        while (it.hasNext()) {
            AddressNode addressNode = (AddressNode) it.next();
            assert addressNode.getBuilding() == null;
            Object buildingRef = addressNode.getBuildingRef();
            if (buildingRef != null) {
                analyzeAddressBuildingByRef(addressNode, newBuildings);
            }
            else {
                analyzeAddressBuildingByGeometry(addressNode, newBuildings);
            }
        }
    }

    /**
     * Use the building reference in the address to find the building to
     * which this address belongs
     * 
     * @param address
     */
    private <T extends AddressNode> void analyzeAddressBuildingByRef(T addressNode, EntityStore<Building> newBuildings) {
        Object buildingRef = addressNode.getBuildingRef();
        Building building = newBuildings.get(buildingRef);
        // TODO create issue if the building is not found
        if (building != null) {
            addressNode.setBuilding(building);
            building.getAddressNodes().add(addressNode);
        }
    }

    /**
     * Use the geometry (point) of this address to find the building to
     * which this address belongs
     * 
     * @param address
     */
    private void analyzeAddressBuildingByGeometry(AddressNode address, EntityStore<Building> newBuildings) {
        Iterator<Building> iterator = newBuildings.iterator();
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
