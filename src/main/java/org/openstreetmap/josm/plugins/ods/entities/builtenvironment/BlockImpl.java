package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.Geometry;

public class BlockImpl implements Block {
    private static Integer nextId = 1;
    private Geometry geometry;
    private Set<Building> buildings = new HashSet<>();
    private Set<AddressNode> addresses = new HashSet<>();
    private boolean incomplete = false;
    private Integer id;
    
    public BlockImpl() {
        this.id = nextId++;

    }
    @Override
    public void build() throws BuildException {
    }

    @Override
    public Class<? extends Entity> getType() {
        return Block.class;
    }

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public boolean isIncomplete() {
        return incomplete;
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void add(Building building) {
        buildings.add(building);
        if (building.isIncomplete()) {
            incomplete = true;
        }
        if (geometry == null) {
            geometry = building.getGeometry();
        }
        else {
            geometry = geometry.union(building.getGeometry());
        }
        building.setBlock(this);
    }
    
    @Override
    public void merge(Block other) {
        geometry = getGeometry().union(other.getGeometry());
        buildings.addAll(other.getBuildings());
        if (other.isIncomplete()) {
            incomplete = true;
        }
    }
    
    public Set<Building> getBuildings() {
        return buildings;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public City getCity() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<AddressNode> getAddresses() {
        return addresses;
    }
    @Override
    
    public String getSource() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Collection<OsmPrimitive> getPrimitives() {
        // TODO Auto-generated method stub
        return null;
    }
}
