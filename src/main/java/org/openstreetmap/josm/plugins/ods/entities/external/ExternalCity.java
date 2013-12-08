package org.openstreetmap.josm.plugins.ods.entities.external;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.City;

import com.vividsolutions.jts.geom.MultiPolygon;

public abstract class ExternalCity extends ExternalEntity implements City {
    protected MultiPolygon geometry;
    protected String name;
    
    @Override
    public String getType() {
        return City.TYPE;
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
    public void createPrimitives(PrimitiveBuilder builder) {
        if (getPrimitives() == null) {
            setPrimitives(builder.build(geometry, getKeys()));
        }
    }

    @Override
    protected Map<String, String> getKeys() {
        Map<String, String> keys = new HashMap<>();
        keys.put("name", name);
        return keys;
    }    
}
