package org.openstreetmap.josm.plugins.ods.entities.external;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.City;

import com.vividsolutions.jts.geom.MultiPolygon;

public abstract class ExternalCity extends ExternalEntity implements City {
    protected MultiPolygon geometry;
    protected String name;
    
    @Override
    public Class<? extends Entity> getType() {
        return City.class;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MultiPolygon getGeometry() {
        return geometry;
    }

    @Override
    protected void buildTags(OsmPrimitive primitive) {
        primitive.put("name", name);
        primitive.put("boundary", "administrative");
        primitive.put("type", "multipolygon");
    }    
}
