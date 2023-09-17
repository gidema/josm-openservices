package org.openstreetmap.josm.plugins.ods.entities.storage;

import java.util.Collections;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.openstreetmap.josm.plugins.ods.entities.GeoObject;

/**
 * The EntityStore stores entities of a single entity type.
 * 
 * @author gertjan
 *
 */
public abstract class AbstractGeoEntityStore<T extends GeoObject> implements EntityStore<T> {
    private Geometry boundary;

    public AbstractGeoEntityStore() {
        super();
    }

    public Geometry getBoundary() {
        if (boundary == null) {
            boundary = new GeometryFactory().buildGeometry(Collections.emptyList());
        }
        return boundary;
    }

    public void extendBoundary(Geometry bounds) {
        if (this.boundary == null) {
            this.boundary = bounds;
        } else {
            this.boundary = this.boundary.union(bounds);
        }
    }

    public abstract GeoIndex<T> getGeoIndex();

}
