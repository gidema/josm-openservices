package org.openstreetmap.josm.plugins.ods.entities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UniqueIndexImpl<T extends Entity> implements Index<T>  {
    private Map<Object, T> map = new HashMap<>();
    private Class<T> clazz;
//    private Class<U> keyClass;
    private Method[] getters;
    private String[] properties; 
    
    public UniqueIndexImpl(Class<T> clazz, String... properties) {
        super();
        this.clazz = clazz;
//        this.keyClass = keyClass;
        this.properties = properties;
        getters = createGetters();
    }
    
    @Override
    public boolean isUnique() {
        return true;
    }


    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.Index#add(T)
     */
    @Override
    public void insert(T entity) {
        Object key = getKey(entity);
        if (key != null) {
            if (map.get(key) != null) {
                System.out.println("Duplicate value for unique index");
                // TODO handle duplicates
            }
            else {
                map.put(key,  entity);
            }
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
    
    public Iterator<T> iterator() {
        return map.values().iterator();
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.Index#get(U)
     */
    public T get(Object key) {
        return map.get(key);
    }
    
    @Override
    public void remove(T entity) {
        map.remove(getKey(entity));
    }

    public void removeByKey(Object key) {
        map.remove(key);
    }

    private Object getKey(T entity) {
        try {
            if (properties.length == 1) {
                return getters[0].invoke(entity);
            }
            else {
                Object[] key = new Object[properties.length];
                for (int i=0; i<properties.length; i++) {
                    key[i] = getters[i].invoke(entity);
                }
                return key;
            }
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
