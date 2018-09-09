package org.openstreetmap.josm.plugins.ods.domains.buildings;

public enum BuildingUnitStatus {
    UNKNOWN, // The status of this building unit is unknown.
    // It could be that a new status type was unrecognized
    PROJECTED, // The building unit is projected
    FUNCTIONAL, // The building unit is ready and the geometry has been verified
    IN_USE_NOT_MEASURED, // The building unit is ready, but the geometry has not yet been verified
    // TODO Create a separate attribute for the geometry quality
    NOT_ESTABLISHED, // The construction of the building unit has been cancelled
    WITHDRAW, // The building unit is no longer present as an independent entity
    // For example because the building was demolished or the building unit combined with a neighboring unit.
    DECLINED,  // The building has been declined and therefore the unit as well
    UNDER_RECONSTRUCTION;


    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
