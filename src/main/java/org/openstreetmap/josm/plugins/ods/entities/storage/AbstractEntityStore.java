package org.openstreetmap.josm.plugins.ods.entities.storage;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * The EntityStore stores entities of a single entity type.
 * 
 * @author gertjan
 *
 */
public abstract class AbstractEntityStore<T extends Object> implements Iterable<T> {
    public AbstractEntityStore() {
        super();
    }

    public abstract PrimaryIndex<T> getPrimaryIndex();

    public abstract List<Index<T>> getAllIndexes();
    
    public void add(T entity) {
        if (getPrimaryIndex().get(entity) == null) {
            for (Index<T> index : getAllIndexes()) {
                index.insert(entity);
            }
        }
    }

    @Override
    public Iterator<T> iterator() {
        return getPrimaryIndex().iterator();
    }

    public Stream<T> stream() {
        return getPrimaryIndex().stream();
    }

    public boolean contains(Object primaryId) {
        return getPrimaryIndex().get(primaryId) != null;
    }
    
    public void remove(T entity) {
        for (Index<T> index : getAllIndexes()) {
            index.remove(entity);
        }
    }

    /**
     * Clear the entity store. Remove all entities
     */
    public void clear() {
        for (Index<T> index : getAllIndexes()) {
            index.clear();
        }
    }
}
