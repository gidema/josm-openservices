package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.entities.EntitySetListener;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;

import com.vividsolutions.jts.geom.Geometry;

public class BuiltEnvironmentEntitySet implements EntitySet {
    private EntitySet entitySet; // The wrapped entitySet
    private EntityStore buildings;
    private EntityStore addresses;
    private EntityStore cities;
    private EntityStore streets;
    
    public BuiltEnvironmentEntitySet(EntitySet entitySet) {
        this.entitySet = entitySet;
        buildings = entitySet.getStore(Building.TYPE);
        addresses = entitySet.getStore(Address.TYPE);
        cities = entitySet.getStore(City.TYPE);
        streets = entitySet.getStore(Street.TYPE);

    }

    @Override
    public void addListener(EntitySetListener listener) {
        entitySet.addListener(listener);
    }

    @Override
    public boolean add(Entity entity) {
        return entitySet.add(entity);
    }

    @Override
    public EntityStore getStore(String entityType) {
        return entitySet.getStore(entityType);
    }


    @Override
    public Geometry getBoundary() {
        return entitySet.getBoundary();
    }

    @Override
    public void extendBoundary(Bounds bounds) {
        entitySet.extendBoundary(bounds);
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
