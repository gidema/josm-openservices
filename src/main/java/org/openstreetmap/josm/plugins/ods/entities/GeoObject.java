package org.openstreetmap.josm.plugins.ods.entities;

import org.locationtech.jts.geom.Geometry;

public interface GeoObject {
    public Geometry getGeometry();
    public void setGeometry(Geometry geometry);
}
