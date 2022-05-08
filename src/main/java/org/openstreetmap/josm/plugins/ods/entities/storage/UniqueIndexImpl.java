package org.openstreetmap.josm.plugins.ods.entities.storage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class UniqueIndexImpl<T extends Object> implements PrimaryIndex<T> {
    private Map<Object, T> map = new HashMap<>();
    private Function<T, ?> getter;
    
    public UniqueIndexImpl(Function<T, ?> getter) {
        super();
        this.getter = getter;
    }
    
    @Override
    public void insert(T entity) {
        Object key = getter.apply(entity);
        if (key != null) {
            map.put(key,  entity);
        }
        else {
            throw new RuntimeException("Could not get an index key");
        }
    }
    
    @Override
    public Iterator<T> iterator() {
        return map.values().iterator();
    }

    @Override
    public Stream<T> stream() {
        return map.values().stream();
    }

    @Override
    public T get(Object key) {
        return map.get(key);
    }
    
    @Override
    public List<T> getAll(Object key) {
        T result = map.get(key);
        if (result == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(result);
    }
    
    @Override
    public void remove(T entity) {
        Object key = getter.apply(entity);
        map.remove(key);
    }

    public void removeByKey(Object key) {
        map.remove(key);
    }

    @Override
    public void clear() {
        map.clear();
    }
}
