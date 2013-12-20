package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;


public interface Address {
    public City getCity();

//    public Serializable getBuildingRef();

//    public void setBuilding(Building building);

//    public Building getBuilding();

    public String getStreetName();

    public Street getStreet();

    public String getPostcode();

    public String getHouseNumber();

    public String getHouseName();

    public String getPlaceName();

    public void setStreet(Street street);
}
