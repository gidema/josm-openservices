package org.openstreetmap.josm.plugins.ods.entities;

public enum EntityStatus {
    UNKNOWN, PLANNED, CONSTRUCTION, IN_USE, IN_USE_NOT_MEASURED, NOT_REALIZED, REMOVAL_DUE, REMOVED;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
