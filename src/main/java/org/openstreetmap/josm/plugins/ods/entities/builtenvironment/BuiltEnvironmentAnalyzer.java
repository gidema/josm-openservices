package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;

public class BuiltEnvironmentAnalyzer {
    protected EntityStore buildings;
    protected EntityStore addresses;
    protected EntityStore cities;
    protected EntityStore streets;
    
    public BuiltEnvironmentAnalyzer(EntitySet entitySet) {
        buildings = entitySet.getStore(Building.TYPE);
        addresses = entitySet.getStore(Address.TYPE);
        cities = entitySet.getStore(City.TYPE);
        streets = entitySet.getStore(Street.TYPE);
    }

    public Street getStreet(String fullName) {
        return (Street) streets.getByName(fullName);
    }

    public City getCity(String cityName) {
        return (City) cities.getByName(cityName);
    }
    
    public EntityStore getBuildings() {
        return buildings;
    }
    
    public EntityStore getAddresses() {
        return addresses;
    }

    public EntityStore getStreets() {
        return streets;
    }
}
