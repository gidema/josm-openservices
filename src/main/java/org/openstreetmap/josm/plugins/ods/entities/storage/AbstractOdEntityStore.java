package org.openstreetmap.josm.plugins.ods.entities.storage;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.Index;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.impl.UniqueIndexImpl;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * The AbstractEntityStore stores entities of a single entity type.
 *
 * @author gertjan
 *
 */
public abstract class AbstractOdEntityStore<T extends OdEntity> implements OdEntityStore<T> {
    private final UniqueIndexImpl<T, ?> primaryIndex = new UniqueIndexImpl<>(OdEntity::getPrimaryId);
    private final List<Index<T>> otherIndexes = new LinkedList<>();
    private Geometry boundary;

    public AbstractOdEntityStore() {
        super();
    }

    protected void addIndex(Index<T> index) {
        otherIndexes.add(index);// TODO Auto-generated method stub
    }

    @Override
    public Iterator<T> iterator() {
        return primaryIndex.iterator();
    }

    @Override
    public GeoIndex<T> getGeoIndex() {
        return null;
    }

    @Override
    public Stream<T> stream() {
        return primaryIndex.stream();
    }

    @Override
    public UniqueIndexImpl<T, ?> getPrimaryIndex() {
        return primaryIndex;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.EntityStore#add(T)
     */
    @Override
    public boolean add(T entity) {
        boolean added = primaryIndex.insert(entity);
        if (added) {
            for (Index<T> index : otherIndexes) {
                index.insert(entity);
            }
        }
        return added;
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
