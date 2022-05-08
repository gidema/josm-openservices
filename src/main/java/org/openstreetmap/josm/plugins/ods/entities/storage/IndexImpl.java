package org.openstreetmap.josm.plugins.ods.entities.storage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

public class IndexImpl<T extends Entity> implements Index<T>  {
    private Map<Object, List<T>> map = new HashMap<>();
    private Class<T> clazz;
    private Method[] getters;
    private String[] properties; 
    private Function<T, ?> getter;
    
    public IndexImpl(Class<T> clazz, String... properties) {
        super();
        this.clazz = clazz;
        this.properties = properties;
        getters = createGetters();
    }
    
    public IndexImpl(Class<T> clazz, Function<T, ?> getter) {
        super();
        this.clazz = clazz;
        this.properties = new String[0];
        this.getter = getter;
        getters = createGetters();
    }
    
    @Override
    public boolean isUnique() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.Index#add(T)
     */
    @Override
    public void insert(T entity) {
        Object key = getKey(entity);
        if (key != null) {
            List<T> list = map.get(key);
            if (list == null) {
                list = new LinkedList<>();
                map.put(key, list);
            }
            list.add(entity);
        }
    }
    
    private Method[] createGetters() {
        Method[] getters = new Method[properties.length];
        try {
            for (int i=0; i< properties.length; i++) {
                getters[i] = clazz.getMethod(getGetterName(i));
            }
            return getters;
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.Index#get(U)
     */
    @Override
    public List<T> getAll(Object key) {
        List<T> result = map.get(key);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }
    
    @Override
    public void remove(T entity) {
        map.remove(getKey(entity));
    }

    private Object getKey(T entity) {
        try {
            if (properties.length == 0) {
                return getter.apply(entity);
            }
            if (properties.length == 1) {
                return getters[0].invoke(entity);
            }
            Object[] key = new Object[properties.length];
            for (int i=0; i<properties.length; i++) {
                key[i] = getters[i].invoke(entity);
            }
            return key;
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void clear() {
        map.clear();
    }

    private String getGetterName(int i) {
        String property = properties[i];
        return "get" + property.substring(0, 1).toUpperCase() +
                    property.substring(1);
    }
}
