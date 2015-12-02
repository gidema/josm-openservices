package org.openstreetmap.josm.plugins.ods.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class OneOrManyList<T> implements OneOrMany<T> {
    private List<? extends T> values;
    
    public OneOrManyList(T value) {
        this.values = Arrays.asList(value);
    }
    
    public OneOrManyList(List<T> values) {
        this.values = values;
    }
    
    @Override
    public boolean hasMany() {
        return values.size() > 1;
    }

    @Override
    public Collection<? extends T> getAll() {
        return values;
    }

    @Override
    public T get() {
        
        return null;
    }
}
