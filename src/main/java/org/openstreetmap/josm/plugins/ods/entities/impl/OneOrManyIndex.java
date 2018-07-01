package org.openstreetmap.josm.plugins.ods.entities.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.Index;

public class OneOrManyIndex<T extends Entity, K> implements Index<T> {
    private final Map<K, ZeroOneMany<T>> entityMap = new HashMap<>();
    private final Function<T, K> keyFactory;

    public OneOrManyIndex(Function<T, K> keyFactory) {
        super();
        this.keyFactory = keyFactory;
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    public Function<T, K> getKeyFactory() {
        return keyFactory;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.Index#add(T)
     */
    @Override
    public boolean insert(T element) {
        K key = keyFactory.apply(element);
        if (key != null) {
            ZeroOneMany<T> zom = entityMap.computeIfAbsent(key, k -> new ZeroOneMany<>());
            return zom.add(element);
        }
        return false;
    }

    public ZeroOneMany<T> get(T template) {
        return get(keyFactory.apply(template));
    }

    public ZeroOneMany<T> get(K key) {
        return entityMap.getOrDefault(key, new ZeroOneMany<>());
    }

    @Override
    public void remove(T entity) {
        ZeroOneMany<T> zom = get(entity);
        zom.remove(entity);
        if (zom.isEmpty()) {
            entityMap.remove(keyFactory.apply(entity));
        }
    }

    public void removeByKey(K key) {
        entityMap.remove(key);
    }

    public K getKey(T element) {
        return keyFactory.apply(element);
    }

    @Override
    public void clear() {
        entityMap.clear();
    }
}
