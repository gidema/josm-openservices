package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.io.Serializable;
import java.util.Iterator;

import org.openstreetmap.josm.plugins.ods.DataLayer;
import org.openstreetmap.josm.plugins.ods.analysis.Analyzer;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalAddressNode;


/**
 * This analyzer builds the relation between address nodes and buildings
 * 
 * @author gertjan
 *
 */
public class AddressToBuildingMatcher implements Analyzer {
    
    public void analyze(DataLayer dataLayer, EntitySet newEntities) {
        BuiltEnvironment bes = new BuiltEnvironment(newEntities);
        EntityStore<AddressNode> newAddresses = bes.getAddresses();
        EntityStore<Building> newBuildings = bes.getBuildings();
        Iterator<AddressNode> it = newAddresses.iterator();
        while (it.hasNext()) {
            ExternalAddressNode addressNode = (ExternalAddressNode) it.next();
            assert addressNode.getBuilding() == null;
            Serializable buildingRef = addressNode.getBuildingRef();
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
    private void analyzeAddressBuildingByRef(ExternalAddressNode address, EntityStore<Building> newBuildings) {
        Serializable buildingRef = address.getBuildingRef();
        Building building = newBuildings.get(buildingRef);
        // TODO create issue if the building is not found
        if (building != null) {
            address.setBuilding(building);
            building.getAddresses().add(address);
        }
    }

    /**
     * Use the geometry (point) of this address to find the building to
     * which this address belongs
     * 
     * @param address
     */
    private void analyzeAddressBuildingByGeometry(ExternalAddressNode address, EntityStore<Building> newBuildings) {
        Iterator<Building> iterator = newBuildings.iterator();
        boolean found = false;
        while (iterator.hasNext() && !found) {
            Building building = iterator.next();
            if (building.getGeometry().covers(address.getGeometry())) {
                address.setBuilding(building);
                building.getAddresses().add(address);
                found = true;
            }
        }
    }
}
