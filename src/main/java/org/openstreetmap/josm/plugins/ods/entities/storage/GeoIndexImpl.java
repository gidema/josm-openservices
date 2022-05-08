package org.openstreetmap.josm.plugins.ods.entities.storage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.index.quadtree.Quadtree;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public class GeoIndexImpl<T extends Entity, V extends T> implements GeoIndex<T>  {
    private Quadtree quadTree = new Quadtree();
    private Class<V> clazz;
    private Method getGeometryMethod;
    private String property;
    
    public GeoIndexImpl(Class<V> clazz, String property) {
        super();
        this.clazz = clazz;
        this.property = property;
        getGeometryMethod = createGetGeometryMethod();
    }
    
    @Override
    public boolean isUnique() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.Index#add(T)
     */
    @Override
    public void insert(T entity) {
        Geometry geom = getGeometry(entity);
        if (geom != null) {
            quadTree.insert(geom.getEnvelopeInternal(), entity);
        }
    }
    
    private Method createGetGeometryMethod() {
        try {
            return clazz.getMethod(getGetterName());
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    @Override
    public List<T> getAll(Object id) {
        throw new UnsupportedOperationException();
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
            if (getGeometry(entity).intersects(geometry)) {
                entities.add(entity);
            }
        }
        return entities;
    }
    
    @Override
    public void remove(T entity) {
        quadTree.remove(getGeometry(entity).getEnvelopeInternal(), entity);
    }

    private Geometry getGeometry(T entity) {
        try {
            return (Geometry)getGeometryMethod.invoke(entity);
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void clear() {
        quadTree = new Quadtree();
    }

    private String getGetterName() {
        return "get" + property.substring(0, 1).toUpperCase() +
                    property.substring(1);
    }
}
