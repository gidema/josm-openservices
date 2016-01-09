package org.openstreetmap.josm.plugins.ods.entities;

public interface EntityPrimitiveBuilder<T> {

    void createPrimitive(T entity);
}
