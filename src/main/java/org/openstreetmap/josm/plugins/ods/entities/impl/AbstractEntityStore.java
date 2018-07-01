package org.openstreetmap.josm.plugins.ods.entities.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.Index;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * The AbstractEntityStore stores entities of a single entity type.
 *
 * @author gertjan
 *
 */
public abstract class AbstractEntityStore<T extends Entity> implements EntityStore<T> {
    private final UniqueIndexImpl<T, ?> primaryIndex;
    private final List<Index<T>> otherIndexes = new LinkedList<>();
    private Geometry boundary;

    public AbstractEntityStore(UniqueIndexImpl<T, ?> primaryIndex) {
        super();
        this.primaryIndex = primaryIndex;
    }

    protected void addIndex(Index<T> index) {
        otherIndexes.add(index);// TODO Auto-generated method stub
    }

    @Override
    public UniqueIndexImpl<T, ?> getPrimaryIndex() {
        return primaryIndex;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.EntityStore#add(T)
     */
    @Override
    public void add(T entity) {
        if (primaryIndex.insert(entity)) {
            for (Index<T> index : otherIndexes) {
                index.insert(entity);
            }
        }
    }


    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.EntityStore#getBoundary()
     */
    @Override
    public Geometry getBoundary() {
        if (boundary == null) {
            boundary = new GeometryFactory().buildGeometry(Collections.emptyList());
        }
        return boundary;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.EntityStore#extendBoundary(com.vividsolutions.jts.geom.Geometry)
     */
    @Override
    public void extendBoundary(Geometry bounds) {
        if (this.boundary == null) {
            this.boundary = bounds;
        } else {
            this.boundary = this.boundary.union(bounds);
        }
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.EntityStore#getGeoIndex()
     */
    @Override
    public abstract GeoIndex<T> getGeoIndex();

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.EntityStore#iterator()
     */
    @Override
    public Iterator<T> iterator() {
        return getPrimaryIndex().iterator();
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.EntityStore#stream()
     */
    @Override
    public Stream<T> stream() {
        return getPrimaryIndex().stream();
    }

    //    /* (non-Javadoc)
    //     * @see org.openstreetmap.josm.plugins.ods.entities.EntityStore#contains(java.lang.Object)
    //     */
    //    @Override
    //    public boolean contains(Object primaryId) {
    //        return getPrimaryIndex().get(primaryId) != null;
    //    }
    //
    //    /* (non-Javadoc)
    //     * @see org.openstreetmap.josm.plugins.ods.entities.EntityStore#getById(java.lang.Object)
    //     */
    //    @Override
    //    public List<T> getById(Object id) {
    //        return getIdIndex().getAll(id);
    //    }
    //
    //    /* (non-Javadoc)
    //     * @see org.openstreetmap.josm.plugins.ods.entities.EntityStore#getByPrimary(java.lang.Object)
    //     */
    //    @Override
    //    public T getByPrimary(Object id) {
    //        return getPrimaryIndex().get(id);
    //    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.EntityStore#remove(T)
     */
    @Override
    public void remove(T entity) {
        primaryIndex.remove(entity);
        for (Index<T> index : otherIndexes) {
            index.remove(entity);
        }
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.EntityStore#clear()
     */
    @Override
    public void clear() {
        primaryIndex.clear();
        for (Index<T> index : otherIndexes) {
            index.clear();
        }
        boundary = null;
    }
}
