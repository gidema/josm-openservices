package org.openstreetmap.josm.plugins.ods.entities;

public enum EntityStatus {
    UNKNOWN, PROJECTED, UNDER_CONSTRUCTION, FUNCTIONAL, IN_USE_NOT_MEASURED, NOT_REALIZED, REMOVAL_DUE, DEMOLISHED, DECLINED;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
