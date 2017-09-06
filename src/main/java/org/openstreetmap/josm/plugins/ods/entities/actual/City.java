package org.openstreetmap.josm.plugins.ods.entities.actual;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;

import com.vividsolutions.jts.geom.MultiPolygon;

public interface City extends Entity {
    String TYPE = "ods:city";

    public String getName();

    @Override
    public MultiPolygon getGeometry();

    @Override
    public EntityType<City> getEntityType();
}
