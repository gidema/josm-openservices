package org.openstreetmap.josm.plugins.ods.entities;

import java.util.stream.Stream;

import org.openstreetmap.josm.plugins.ods.entities.impl.UniqueIndexImpl;

import com.vividsolutions.jts.geom.Geometry;

public interface EntityStore<T extends Entity> extends Iterable<T> {

    public void add(T entity);

    public Geometry getBoundary();

    public void extendBoundary(Geometry bounds);

    public UniqueIndexImpl<T, ?> getPrimaryIndex();

    public GeoIndex<T> getGeoIndex();

    public Stream<T> stream();

    //    public boolean contains(K primaryId);

    //    public T getByPrimary(K id);

    public void remove(T entity);

    /**
     * Clear the entity store. Remove all entities
     */
    public void clear();

}