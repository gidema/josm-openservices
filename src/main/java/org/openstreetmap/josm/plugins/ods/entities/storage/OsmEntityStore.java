package org.openstreetmap.josm.plugins.ods.entities.storage;

import java.util.stream.Stream;

import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

import com.vividsolutions.jts.geom.Geometry;

public interface OsmEntityStore<T extends OsmEntity> extends Iterable<T> {

    public void add(T entity);

    public Geometry getBoundary();

    public void extendBoundary(Geometry bounds);

    public T get(Long primitiveId);

    public GeoIndex<T> getGeoIndex();

    public Stream<T> stream();

    public boolean contains(Long primitiveId);

    public void remove(T entity);

    /**
     * Clear the entity store. Remove all entities
     */
    public void clear();

}