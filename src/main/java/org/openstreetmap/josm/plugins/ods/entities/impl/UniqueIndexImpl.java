package org.openstreetmap.josm.plugins.ods.entities.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.Index;
import org.openstreetmap.josm.tools.Logging;

public class UniqueIndexImpl<T extends Entity, K> implements Index<T> {
    private final Map<K, T> entityMap = new HashMap<>();
    private final Function<T, K> keyFactory;

    public UniqueIndexImpl(Function<T, K> keyFactory) {
        super();
        this.keyFactory = keyFactory;
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    public Function<T, K> getKeyFactory() {
        return keyFactory;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.Index#add(T)
     */
    @Override
    public boolean insert(T entity) {
        K key = keyFactory.apply(entity);
        if (key != null) {
            T previous = entityMap.putIfAbsent(key, entity);
            if (previous != null && !previous.equals(entity)) {
                Logging.warn("Duplicate value for unique index " + key.toString() + " of " + entity.getClass().getSimpleName());
                return false;
            }
            return true;
        }
        return false;
    }

    public Iterator<T> iterator() {
        return entityMap.values().iterator();
    }

    public Stream<T> stream() {
        return entityMap.values().stream();
    }

    public T get(K key) {
        return entityMap.get(key);
    }

    @Override
    public void remove(T entity) {
        entityMap.remove(keyFactory.apply(entity));
    }

    @Override
    public void clear() {
        entityMap.clear();
    }
}
