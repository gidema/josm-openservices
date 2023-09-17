package org.openstreetmap.josm.plugins.ods.entities.storage;

import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.plugins.ods.entities.GeoObject;

/**
 * The GeoCapable extends Entity stores with geometry capabilities.
 * 
 * @author gertjan
 *
 */
public interface GeoCapable<T extends GeoObject> {

    public Geometry getBoundary();

    public void extendBoundary(Geometry bounds);

    public abstract GeoIndex<T> getGeoIndex();
}
