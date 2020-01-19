package org.openstreetmap.josm.plugins.ods;

import java.util.IdentityHashMap;
import java.util.Map;

public class ParameterSet {
    private final Map<Object, Object> parameters = new IdentityHashMap<>();

    public <T> ParameterSet put(ParameterType<T> key, T value) {
        parameters.put(key, value);
        return this;
    }

    public <T> T get(ParameterType<T> key) {
        @SuppressWarnings("unchecked")
        T value = (T) parameters.get(key);
        return value;
    }

    public <T> T get(ParameterType<T> key, T defaultValue) {
        @SuppressWarnings("unchecked")
        T value = (T) parameters.get(key);
        return value != null ? value : defaultValue;
    }
}
