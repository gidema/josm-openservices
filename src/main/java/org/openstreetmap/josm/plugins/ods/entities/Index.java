package org.openstreetmap.josm.plugins.ods.entities;

public interface Index<T> {

    public boolean isUnique();
    
    public void insert(T entity);
    
    public void remove(T entity);

    public void clear();
    
}