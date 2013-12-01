package org.openstreetmap.josm.plugins.ods.entities.imported;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Place;

import com.vividsolutions.jts.geom.MultiPolygon;

public abstract class ImportedCity extends ImportedEntity implements Place {
    protected MultiPolygon geometry;
    protected String name;
    
    @Override
    public String getNamespace() {
        return Place.NAMESPACE;
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
