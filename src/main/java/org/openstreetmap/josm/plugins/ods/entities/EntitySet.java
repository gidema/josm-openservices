package org.openstreetmap.josm.plugins.ods.entities;

import org.openstreetmap.josm.data.Bounds;

import com.vividsolutions.jts.geom.Geometry;

public interface EntitySet {
    public void addListener(EntitySetListener listener);
    
    public boolean add(Entity entity);
    
    public EntityStore getStore(String entityType);
    
    public Geometry getBoundary();

    public void extendBoundary(Bounds bounds);

}