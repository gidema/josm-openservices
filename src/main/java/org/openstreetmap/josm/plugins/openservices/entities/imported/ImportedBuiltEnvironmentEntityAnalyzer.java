package org.openstreetmap.josm.plugins.openservices.entities.imported;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.openservices.entities.Entity;
import org.openstreetmap.josm.plugins.openservices.entities.EntitySet;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Street;

/**
 * The ImportedBuiltEnvironmentEntityAnalyzer analyzes buildings, addresses and related
 * objects like streets and cities.
 * 
 * @author gertjan
 * 
 */
public class ImportedBuiltEnvironmentEntityAnalyzer implements ImportedEntityAnalyzer {
    private EntitySet entitySet;

    /**
     * 
     */
    @Override
    public void setEntitySet(EntitySet entitySet) {
        this.entitySet = entitySet;
    }

    public EntitySet getEntitySet() {
        return entitySet;
    }

    @Override
    public void analyzeNewEntities(Collection<Entity> entities, Bounds bounds) {
        List<ImportedBuilding> newBuildings = new LinkedList<ImportedBuilding>();
        List<ImportedAddress> newAddresses = new LinkedList<ImportedAddress>();

        for (Entity entity : entities) {
            if (entity instanceof ImportedBuilding) {
                newBuildings.add((ImportedBuilding) entity);
            } else if (entity instanceof ImportedAddress) {
                newAddresses.add((ImportedAddress) entity);
            }
        }
        analyzeAddressStreets(newAddresses);
        analyzeAddressBuildings(newAddresses);
    }

    protected void analyzeAddressStreets(List<ImportedAddress> newAddresses) {
        for (ImportedAddress address : newAddresses) {
            String streetName = address.getStreetName();
            if (streetName != null) {
                List<Street> streets = getEntitySet().getStreets().getByName(
                        streetName);
                Street street = null;
                if (streets.isEmpty()) {
                    street = new ImportedStreet(streetName);
                    entitySet.add(street);
                } else if (streets.size() == 1) {
                    street = streets.get(0);
                }
                address.setStreet(street);
                street.getAddresses().add(address);
            }
        }
    }

    protected void analyzeAddressBuildings(List<ImportedAddress> newAddresses) {
        for (ImportedAddress address : newAddresses) {
            assert address.getBuilding() == null;
            Serializable buildingRef = address.getBuildingRef();
            if (buildingRef != null) {
                analyzeAddressBuildingByRef(address);
            }
            else {
                analyzeAddressBuildingByGeometry(address);
            }
        }
    }

    /**
     * Use the building reference in the address to find the building to
     * which this address belongs
     * 
     * @param address
     */
    private void analyzeAddressBuildingByRef(ImportedAddress address) {
        Serializable buildingRef = address.getBuildingRef();
        Building building = entitySet.getBuildings().get(buildingRef);
        address.setBuilding(building);
        building.getAddresses().add(address);
    }

    /**
     * Use the geometry (point) of this address to find the building to
     * which this address belongs
     * 
     * @param address
     */
    private void analyzeAddressBuildingByGeometry(ImportedAddress address) {
        Iterator<Building> iterator = entitySet.getBuildings().iterator();
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

    protected void analyzeBuildingCompleteness(ImportedBuilding entity) {
        // TODO Auto-generated method stub

    }

    protected void analyzeAddress(ImportedAddress entity) {
        // TODO Auto-generated method stub

    }

}
