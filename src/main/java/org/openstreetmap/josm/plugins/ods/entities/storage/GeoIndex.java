package org.openstreetmap.josm.plugins.ods.entities.storage;

import java.util.List;

import org.locationtech.jts.geom.Geometry;

public interface GeoIndex<T> extends Index<T> {

    public List<T> intersection(Geometry geometry);

}