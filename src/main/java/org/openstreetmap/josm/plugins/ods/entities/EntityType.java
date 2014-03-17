package org.openstreetmap.josm.plugins.ods.entities;

public interface EntityType {
    public String getName();
    public boolean hasReferenceId();
    public boolean hasName();
    public boolean hasGeometry();
}
