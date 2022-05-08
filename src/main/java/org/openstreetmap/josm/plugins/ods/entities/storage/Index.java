package org.openstreetmap.josm.plugins.ods.entities.storage;

import java.util.List;

public interface Index<T> {

    public boolean isUnique();
    
    public void insert(T entity);
    
    public List<T> getAll(Object id);
    
    public void remove(T entity);

    public void clear();
    
}