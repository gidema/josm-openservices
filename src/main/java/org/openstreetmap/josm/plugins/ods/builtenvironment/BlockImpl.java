package org.openstreetmap.josm.plugins.ods.builtenvironment;

import java.util.HashSet;
import java.util.Set;

import com.vividsolutions.jts.geom.Geometry;

public class BlockImpl implements Block {
    private static Integer nextId = 1;
    
    private BlockStore store;
    private Geometry geometry;
    private Set<Building> internalBuildings = new HashSet<>();
    private Set<Building> externalBuildings = new HashSet<>();
    private Set<AddressNode> addresses = new HashSet<>();
    private boolean incomplete = false;
    private Integer id;
    
    public BlockImpl(BlockStore store) {
        this.id = nextId++;
        this.store = store;
    }
    
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public BlockStore getStore() {
        return store;
    }

    @Override
    public boolean isIncomplete() {
        return incomplete;
    }

    @Override
    public void add(Building building) {
        if (building.isInternal()) {
            internalBuildings.add(building);
        }
        else {
            externalBuildings.add(building);
        }
        if (building.isIncomplete()) {
            incomplete = true;
        }
        if (geometry == null) {
            geometry = building.getGeometry();
        }
        else {
            geometry = geometry.union(building.getGeometry());
        }
//        building.setBlock(this);
    }
    
    @Override
    public void merge(Block other) {
        geometry = getGeometry().union(other.getGeometry());
        for (Building building : other.getInternalBuildings()) {
            internalBuildings.add(building);
//            building.setBlock(this);
        }
        for (Building building : other.getExternalBuildings()) {
            internalBuildings.add(building);
//            building.setBlock(this);
        }
        if (other.isIncomplete()) {
            incomplete = true;
        }
    }
    
    @Override
    public Set<Building> getInternalBuildings() {
        return internalBuildings;
    }

    @Override
    public Set<Building> getExternalBuildings() {
        return externalBuildings;
    }

    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public Set<AddressNode> getAddresses() {
        return addresses;
    }
}
