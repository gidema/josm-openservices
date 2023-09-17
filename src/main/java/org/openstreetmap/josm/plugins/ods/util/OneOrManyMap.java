package org.openstreetmap.josm.plugins.ods.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class OneOrManyMap<T> implements OneOrMany<T> {
    private final Function<T, Long> idFunction;
    private final T firstValue;
    private final Long firstId;
    private Map<Long, T> values = null;
    
    public OneOrManyMap(T value, Function<T, Long> idFunction) {
        this.firstValue = value;
        this.idFunction = idFunction;
        this.firstId = idFunction.apply(firstValue);
    }

    @Override
    public void add(T value) {
        Long id = idFunction.apply(value);
        if (id.equals(firstId)) return;
        if (values == null) {
            values = new HashMap<>();
            values.put(firstId, firstValue);
        }
        values.put(idFunction.apply(value), value);
    }
    
    @Override
    public boolean hasMany() {
        return values != null;
    }

    @Override
    public Collection<? extends T> getAll() {
        return values == null ? Collections.singleton(firstValue) : values.values();
    }

    @Override
    public T get() {
        if (hasMany()) throw new UnsupportedOperationException("There ar more then 1 values");
        return firstValue;
    }
}
