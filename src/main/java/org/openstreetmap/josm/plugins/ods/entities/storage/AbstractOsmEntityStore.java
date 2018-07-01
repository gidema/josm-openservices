package org.openstreetmap.josm.plugins.ods.entities.storage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.Index;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * The AbstractEntityStore stores entities of a single entity type.
 *
 * @author gertjan
 *
 */
public abstract class AbstractOsmEntityStore<T extends OsmEntity> implements OsmEntityStore<T> {
    private final Map<Long, T> primitiveIndex = new HashMap<>();
    private final List<Index<T>> otherIndexes = new LinkedList<>();
    private Geometry boundary;

    public AbstractOsmEntityStore() {
        super();
    }

    protected void addIndex(Index<T> index) {
        otherIndexes.add(index);// TODO Auto-generated method stub
    }

    @Override
    public T get(Long primitiveId) {
        return primitiveIndex.get(primitiveId);
    }

    @Override
    public GeoIndex<T> getGeoIndex() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return primitiveIndex.values().iterator();
    }

    @Override
    public Stream<T> stream() {
        return primitiveIndex.values().stream();
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.EntityStore#add(T)
     */
    @Override
    public void add(T entity) {
        T existing = primitiveIndex.putIfAbsent(entity.getPrimitiveId(), entity);
        if (existing == null) {
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
     * @see org.openstreetmap.josm.plugins.ods.entities.storage.OsmEntityStore#extendBoundary(com.vividsolutions.jts.geom.Geometry)
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
     * @see org.openstreetmap.josm.plugins.ods.entities.EntityStore#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Long primitiveId) {
        return primitiveIndex.containsKey(primitiveId);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.EntityStore#remove(T)
     */
    @Override
    public void remove(T entity) {
        primitiveIndex.remove(entity.getPrimitiveId());
        for (Index<T> index : otherIndexes) {
            index.remove(entity);
        }
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.EntityStore#clear()
     */
    @Override
    public void clear() {
        primitiveIndex.clear();
        for (Index<T> index : otherIndexes) {
            index.clear();
        }
        boundary = null;
    }
}
