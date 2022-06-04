package org.openstreetmap.josm.plugins.ods.entities.storage;

import java.util.stream.Stream;

/**
 * The PrimaryIndex is has some additions to Index interface:
 * 
 * The key for the primary index must be unique.
 * If a object is inserted with a key that already exist, the new object is ignored.
 * Because each key is unique, we can implement a get() function that return a most 1 value for a key. 
 * The index can implement the Iterable interface.
 * We can also return a stream of all entities
 * 
 * @author Idema
 *
 * @param <T>
 */
public interface PrimaryIndex<T> extends Index<T>, Iterable<T> {

    @Override
    default boolean isUnique() {
        return false;
    }

    public boolean contains(T entity);

    public T get(Object Key);

    public Stream<T> stream();
}
