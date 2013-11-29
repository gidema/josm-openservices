package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.entities.EntitySetListener;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;

import com.vividsolutions.jts.geom.Geometry;

public class BuiltEnvironmentEntitySet implements EntitySet {
    private EntitySet entitySet; // The wrapped entitySet
    private EntityStore<Building> buildings;
    private EntityStore<Address> addresses;
    private EntityStore<Place> cities;
    private EntityStore<Street> streets;
    
    public BuiltEnvironmentEntitySet(EntitySet entitySet) {
        this.entitySet = entitySet;
        buildings = entitySet.getStore(Building.NAMESPACE);
        addresses = entitySet.getStore(Address.NAMESPACE);
        cities = entitySet.getStore(Place.NAMESPACE);
        streets = entitySet.getStore(Street.NAMESPACE);

    }

    @Override
    public void addListener(EntitySetListener listener) {
        entitySet.addListener(listener);
    }

    @Override
    public <T extends Entity> boolean add(T entity) {
        return entitySet.add(entity);
    }

    @Override
    public <T extends Entity> EntityStore<T> getStore(String nameSpace) {
        return entitySet.getStore(nameSpace);
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
