package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

public enum BuildingType {
    UNCLASSIFIED, HOUSE, HOUSEBOAT, STATIC_CARAVAN, INDUSTRIAL, 
    RETAIL, OFFICE, APARTMENTS, GARAGE, SUBSTATION, PRISON, OTHER;
    
    private String subType;

    public String getSubType() {
        return subType;
    }

    public static BuildingType OTHER(String subType) {
        BuildingType other = BuildingType.OTHER;
        other.subType = subType;
        return other;
    }
}
