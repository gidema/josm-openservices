package org.openstreetmap.josm.plugins.openservices.entities.builtenvironment;

import org.openstreetmap.josm.plugins.openservices.entities.EntitySet;
import org.openstreetmap.josm.plugins.openservices.entities.EntityStore;

public class BuiltEnvironmentAnalyzer {
    protected EntityStore<Building> buildings;
    protected EntityStore<Address> addresses;
    protected EntityStore<Place> cities;
    protected EntityStore<Street> streets;
    
    public BuiltEnvironmentAnalyzer(EntitySet entitySet) {
        buildings = entitySet.getStore(Building.NAMESPACE);
        addresses = entitySet.getStore(Address.NAMESPACE);
        cities = entitySet.getStore(Place.NAMESPACE);
        streets = entitySet.getStore(Street.NAMESPACE);
    }

    public Street getStreet(String fullName) {
        return streets.getByName(fullName);
    }

    public Place getPlace(String cityName) {
        return cities.getByName(cityName);
    }
    
    public EntityStore<Building> getBuildings() {
        return buildings;
    }
    
    public EntityStore<Address> getAddresses() {
        return addresses;
    }

    public EntityStore<Street> getStreets() {
        return streets;
    }
}
