package org.openstreetmap.josm.plugins.ods.domains.places;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;

import org.locationtech.jts.geom.MultiPolygon;

public interface OdCity extends OdEntity {

    String TYPE = "ods:city";

    public String getName();

    @Override
    public MultiPolygon getGeometry();
}
