package org.openstreetmap.josm.plugins.ods.domains.buildings;

public interface OdAddress extends Address {
//    public void setHouseNumber(HouseNumber houseNumber);

    public void setStreetName(String streetName);

    public void setPostcode(String postcode);

    public void setCityName(String cityName);
}
