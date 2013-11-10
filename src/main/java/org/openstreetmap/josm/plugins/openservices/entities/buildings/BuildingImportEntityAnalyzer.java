package org.openstreetmap.josm.plugins.openservices.entities.buildings;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.openservices.entities.Entity;
import org.openstreetmap.josm.plugins.openservices.entities.EntitySet;
import org.openstreetmap.josm.plugins.openservices.entities.ImportEntityAnalyzer;

/**
 * The BuildingImportEntityAnalyzer analyzes buildings, addresses and related
 * objects like streets and cities.
 * 
 * @author gertjan
 * 
 */
public class BuildingImportEntityAnalyzer implements ImportEntityAnalyzer {
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
        List<ImportBuilding> newBuildings = new LinkedList<ImportBuilding>();
        List<ImportAddress> newAddresses = new LinkedList<ImportAddress>();

        for (Entity entity : entities) {
            if (entity instanceof ImportBuilding) {
                newBuildings.add((ImportBuilding) entity);
            } else if (entity instanceof ImportAddress) {
                newAddresses.add((ImportAddress) entity);
            }
        }
        analyzeAddressStreets(newAddresses);
        analyzeAddressBuildings(newAddresses);
    }

    protected void analyzeAddressStreets(List<ImportAddress> newAddresses) {
        for (ImportAddress address : newAddresses) {
            String streetName = address.getStreetName();
            if (streetName != null) {
                List<Street> streets = getEntitySet().getStreets().getByName(
                        streetName);
                Street street = null;
                if (streets.isEmpty()) {
                    street = new ImportStreet(streetName);
                    entitySet.add(street);
                } else if (streets.size() == 1) {
                    street = streets.get(0);
                }
                address.setStreet(street);
                street.getAddresses().add(address);
            }
        }
    }

    protected void analyzeAddressBuildings(List<ImportAddress> newAddresses) {
        for (ImportAddress address : newAddresses) {
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
    private void analyzeAddressBuildingByRef(ImportAddress address) {
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
    private void analyzeAddressBuildingByGeometry(ImportAddress address) {
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

    protected void analyzeBuildingCompleteness(ImportBuilding entity) {
        // TODO Auto-generated method stub

    }

    protected void analyzeAddress(ImportAddress entity) {
        // TODO Auto-generated method stub

    }

}
