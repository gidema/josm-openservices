package org.openstreetmap.josm.plugins.ods.entities;

import java.util.Iterator;

import org.openstreetmap.josm.data.Bounds;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public interface EntitySet {
    
    public <T extends Entity> boolean add(T entity);
    
    public <T extends Entity> EntityStore<T> getStore(Class<? extends Entity> entityType);
    
    /**
     * Retrieve an iterator that iterates over all stores
     *  
     * @return
     */
    public Iterator<EntityStore<? extends Entity>> stores();
    
    public Geometry getBoundary();

    public void extendBoundary(Polygon bounds);

    public void extendBoundary(Bounds bounds);

}