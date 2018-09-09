package org.openstreetmap.josm.plugins.ods.domains.buildings;

public enum BuildingStatus {
    UNKNOWN, // The reported status was unknown
    PROJECTED, // The building is projected, but construction hasn't started. NL: Bouwvergunning verleend
    UNDER_CONSTRUCTION, // The building is under construction. NL: Bouw gestart
    FUNCTIONAL, // The building is fully operational. NL: Pand in gebruik
    IN_USE_NOT_MEASURED, // The building is operational, but hasn't been measured yet
    // NL: Pand in gebruik (niet ingemeten)
    // TODO: Create a separate attribute for the geometry quality
    NOT_ESTABLISHED, // Construction of the building has been cancelled NL: Niet gerealiseerd pand
    DEMOLITION_DUE, // The building will be demolished in the near future. NL: Sloopvergunning verleend
    DEMOLISHED, // The building has been demolished
    DECLINED, // The building is out of use because of the bad state. NL: Pand buiten gebruik
    UNDER_RECONSTRUCTION, // The building is under reconstruction. NL: Verbouwing pand;
    WITHDRAWN; // The building has been withdrawn from the official registry because is should't
    // have been there in the first place. The building may still be present and valid in OSM
    // NL: Pand ten onrechte opgevoerd

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
