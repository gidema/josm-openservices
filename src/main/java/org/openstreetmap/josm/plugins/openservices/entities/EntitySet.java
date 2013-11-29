package org.openstreetmap.josm.plugins.openservices.entities;

import org.openstreetmap.josm.data.Bounds;

import com.vividsolutions.jts.geom.Geometry;

public interface EntitySet {
    public void addListener(EntitySetListener listener);
    
    public <T extends Entity> boolean add(T entity);
    
    public <T extends Entity> EntityStore<T> getStore(String nameSpace);
    
    public Geometry getBoundary();

    public void extendBoundary(Bounds bounds);

}