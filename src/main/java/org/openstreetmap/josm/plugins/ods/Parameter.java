package org.openstreetmap.josm.plugins.ods;

public class Parameter<T> {
    private final ParameterType<T> key;
    private final T value;

    public Parameter(ParameterType<T> key, T value) {
        super();
        this.key = key;
        this.value = value;
    }

    public ParameterType<T> getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }
}
