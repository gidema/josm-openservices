package org.openstreetmap.josm.plugins.ods;

public class ParameterType<T> {
    private final Class<T> type;

    public ParameterType(Class<T> type) {
        super();
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    public static ParameterType<String> STRING() {
        return new ParameterType<>(String.class);
    }

    public static ParameterType<Integer> INTEGER() {
        return new ParameterType<>(Integer.class);
    }

    public static ParameterType<Boolean> BOOLEAN() {
        return new ParameterType<>(Boolean.class);
    }
}
