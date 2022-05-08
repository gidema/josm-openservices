package org.openstreetmap.josm.plugins.ods.domains.places;

import org.locationtech.jts.geom.MultiPolygon;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

public interface OsmCity extends OsmEntity {
    String TYPE = "ods:city";

    public Long getCityId();
    
    public String getName();

    @Override
    public MultiPolygon getGeometry();
}
