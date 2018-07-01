package org.openstreetmap.josm.plugins.ods.domains.places;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;

import com.vividsolutions.jts.geom.MultiPolygon;

public interface OdCity extends OdEntity {
    public Long getCityId();

    public String getName();

    @Override
    public MultiPolygon getGeometry();
}
