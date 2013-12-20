package org.openstreetmap.josm.plugins.ods.entities.builtenvironment.test;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.crs.GeoUtil;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Block;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.City;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

public class MockBuilding implements Building {
    private Geometry geometry;
    
    public MockBuilding(String wkt) throws ParseException {
        geometry = GeoUtil.OSM_WKT_READER.read(wkt);
    }

    @Override
    public void build() throws BuildException {
        // TODO Auto-generated method stub

    }

    @Override
    public Class<? extends Entity> getType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isInternal() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isIncomplete() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDeleted() {
        // TODO Auto-generated method stub
        return false;
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
    public void createPrimitives(PrimitiveBuilder primitiveBuilder) {
        // TODO Auto-generated method stub

    }

    @Override
    public Collection<OsmPrimitive> getPrimitives() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setGeometry(Geometry geometry) {
        // TODO Auto-generated method stub

    }

    @Override
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Block getBlock() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setIncomplete(boolean incomplete) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isUnderConstruction() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getStartDate() {
        // TODO Auto-generated method stub
        return null;
    }

}
