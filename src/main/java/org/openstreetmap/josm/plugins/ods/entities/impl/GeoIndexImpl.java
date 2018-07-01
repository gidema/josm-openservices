package org.openstreetmap.josm.plugins.ods.entities.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class GeoIndexImpl<T extends Entity> implements GeoIndex<T>  {
    private final Class<T> clazz;
    private final Function<T, Geometry> keyFactory;
    private Quadtree quadTree = new Quadtree();

    public GeoIndexImpl(Class<T> clazz, Function<T, Geometry> keyFactory) {
        super();
        this.clazz = clazz;
        this.keyFactory = keyFactory;
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.Index#add(T)
     */
    @Override
    public boolean insert(T entity) {
        Geometry geom = keyFactory.apply(entity);
        if (geom != null) {
            quadTree.insert(geom.getEnvelopeInternal(), entity);
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.GeoIndex#intersection(com.vividsolutions.jts.geom.Geometry)
     */
    @Override
    public List<T> intersection(Geometry geometry) {
        List<T> entities = new LinkedList<>();
        List<?> candidates = quadTree.query(geometry.getEnvelopeInternal());
        for (Object object : candidates) {
            T entity = clazz.cast(object);
            if (keyFactory.apply(entity).intersects(geometry)) {
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public void remove(T entity) {
        quadTree.remove(keyFactory.apply(entity).getEnvelopeInternal(), entity);
    }

    @Override
    public void clear() {
        quadTree = new Quadtree();
    }
}
