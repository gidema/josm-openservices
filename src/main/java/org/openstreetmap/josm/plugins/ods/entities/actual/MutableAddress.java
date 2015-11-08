package org.openstreetmap.josm.plugins.ods.entities.actual;

public interface MutableAddress extends Address {

    void setHouseName(String houseName);

    void setCityName(String cityName);

    void setStreetName(String streetName);

    void setPostcode(String postcode);

    void setHouseNumber(Integer houseNumber);

    void setHouseLetter(Character houseLetter);
    
    void setHouseNumberExtra(String extra);

    void setFullHouseNumber(String fullHouseNumber);

    void setStreet(Street street);
}
