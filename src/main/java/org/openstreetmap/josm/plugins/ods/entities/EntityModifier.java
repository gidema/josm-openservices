package org.openstreetmap.josm.plugins.ods.entities;

public interface EntityModifier<T> {
    public void modify(T entity);
    public Class<T> getTargetType();
    boolean isApplicable(T target);
}
