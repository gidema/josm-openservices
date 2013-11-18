package org.openstreetmap.josm.plugins.openservices.entities.josm;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.openservices.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.openservices.entities.EntitySet;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Block;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Place;

import com.vividsolutions.jts.geom.Polygon;

public class JosmBuilding implements Building {

    public JosmBuilding() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getNamespace() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Serializable getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setEntitySet(EntitySet entitySet) {
        // TODO Auto-generated method stub

    }

    @Override
    public EntitySet getEntitySet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void createPrimitives(PrimitiveBuilder primitiveBuilder) {
        // TODO Auto-generated method stub

    }

    @Override
    public Collection<OsmPrimitive> getPrimitives() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Polygon getGeometry() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Place getPlace() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Address> getAddresses() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Block getBlock() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isComplete() {
        // TODO Auto-generated method stub
        return false;
    }

}
