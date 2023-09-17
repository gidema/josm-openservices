package org.openstreetmap.josm.plugins.ods.entities.storage;

import java.util.stream.Stream;

public interface EntityStore<T> extends Iterable<T> {
//    public PrimaryIndex<T> getPrimaryIndex();

    public void add(T entity);

    public Stream<T> stream();

//    public boolean contains(Object primaryId);
    
    public void remove(T entity);

    /**
     * Clear the entity store. Remove all entities
     */
    public void clear();

}
