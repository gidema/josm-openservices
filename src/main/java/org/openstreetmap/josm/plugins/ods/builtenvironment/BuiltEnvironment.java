package org.openstreetmap.josm.plugins.ods.builtenvironment;

import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Convenience wrapper around an entity set for easy access to
 * Buildings, AddressNodes etc.
 * 
 * @author gertjan
 *
 */
public class BuiltEnvironment  {
    private EntitySet entitySet; // The wrapped environment
    private EntityStore<Building> buildings;
    private EntityStore<AddressNode> addresses;
    private EntityStore<City> cities;
    private EntityStore<Street> streets;
//    private EntityStore<AssociatedStreet> associatedStreets;
    
    public BuiltEnvironment(EntitySet entitySet) {
        this.entitySet = entitySet;
        buildings = entitySet.getStore(Building.class);
        addresses = entitySet.getStore(AddressNode.class);
        cities = entitySet.getStore(City.class);
        streets = entitySet.getStore(Street.class);
    }

    public EntitySet getEntitySet() {
        return entitySet;
    }
    
    public Geometry getBoundary() {
        return entitySet.getBoundary();
    }

    public Street getStreet(String fullName) {
        return (Street) streets.getByName(fullName);
    }

    public City getCity(String cityName) {
        return (City) cities.getByName(cityName);
    }
    
    public EntityStore<Building> getBuildings() {
        return buildings;
    }
    
    public EntityStore<AddressNode> getAddresses() {
        return addresses;
    }

    public EntityStore<Street> getStreets() {
        return streets;
    }
}
