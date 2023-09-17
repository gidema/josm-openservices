package org.openstreetmap.josm.plugins.ods.context;

public interface Provider<T> {
    public T getComponent(OdsContext context);
}
