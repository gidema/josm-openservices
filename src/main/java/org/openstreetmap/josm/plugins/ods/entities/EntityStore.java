package org.openstreetmap.josm.plugins.ods.entities;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.vividsolutions.jts.geom.Geometry;

/**
 * The EntityStore stores entities of a single entity type.
 * 
 * @author gertjan
 *
 */
public abstract class EntityStore<T extends Entity> implements Iterable<T> {
    private List<Index<T>> indexes = new LinkedList<>();
    private Geometry boundary;

    protected List<Index<T>> getIndexes() {
        return indexes;
    }

    public void add(T entity) {
        if (getByPrimary(entity.getPrimaryId()) == null) {
            for (Index<T> index : indexes) {
                index.insert(entity);
            }
        }
    }

    public Geometry getBoundary() {
        return boundary;
    }

    public void extendBoundary(Geometry boundary) {
        if (this.boundary == null) {
            this.boundary = boundary;
        } else {
            this.boundary = this.boundary.union(boundary);
        }
    }

    public abstract UniqueIndexImpl<T> getPrimaryIndex();

    public abstract Index<T> getIdIndex();

    public abstract GeoIndex<T> getGeoIndex();

    public Iterator<T> iterator() {
        return getPrimaryIndex().iterator();
    }

    public Stream<T> stream() {
        return getPrimaryIndex().stream();
    }

    public List<T> getById(Object id) {
        return getIdIndex().getAll(id);
    }

    public T getByPrimary(Object id) {
        return getPrimaryIndex().get(id);
    }

    public void remove(T entity) {
        for (Index<T> index : indexes) {
            index.remove(entity);
        }
    }

    /**
     * Clear the entity store. Remove all entities
     */
    public void clear() {
        for (Index<T> index : indexes) {
            index.clear();
        }
    }
}
