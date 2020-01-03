package org.openstreetmap.josm.plugins.ods.entities;

public interface EntityModifier<T> {
    public void modify(T address);
    public Class<T> getTargetType();
    boolean isApplicable(T target);
}
