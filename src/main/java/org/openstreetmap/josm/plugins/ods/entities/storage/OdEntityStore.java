package org.openstreetmap.josm.plugins.ods.entities.storage;

import java.util.stream.Stream;

import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.impl.UniqueIndexImpl;

import com.vividsolutions.jts.geom.Geometry;

public interface OdEntityStore<T extends OdEntity, K> extends Iterable<T> {

    public boolean add(T entity);

    public T get(K primaryKey);

    public boolean contains(K primaryKey);

    //    public void remove(K primaryKey);

    public void remove(T entity);

    public Geometry getBoundary();

    public void extendBoundary(Geometry bounds);

    public UniqueIndexImpl<T, K> getPrimaryIndex();

    public GeoIndex<T> getGeoIndex();

    public Stream<T> stream();

    /**
     * Clear the entity store. Remove all entities
     */
    public void clear();

}