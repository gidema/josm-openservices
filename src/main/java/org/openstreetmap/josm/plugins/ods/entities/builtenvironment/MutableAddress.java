package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

public interface MutableAddress extends Address {

    void setHouseName(String houseName);

    void setCityName(String cityName);

    void setStreetName(String streetName);

    void setPostcode(String postcode);

    void setHouseNumber(Integer houseNumber);

    void setFullHouseNumber(String fullHouseNumber);

    void setStreet(Street street);
}
