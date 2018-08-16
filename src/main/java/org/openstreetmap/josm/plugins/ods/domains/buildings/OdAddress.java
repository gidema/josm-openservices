package org.openstreetmap.josm.plugins.ods.domains.buildings;

import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.Address;
import org.openstreetmap.josm.plugins.ods.domains.places.OdCity;
import org.openstreetmap.josm.plugins.ods.domains.streets.OdStreet;

public interface OdAddress extends Address {
    @Override
    public Integer getHouseNumber();

    @Override
    public Character getHouseLetter();

    @Override
    public String getHouseNumberExtra();

    @Override
    public String getFullHouseNumber();

    @Override
    public String getStreetName();

    public OdStreet getStreet();

    @Override
    public String getPostcode();

    @Override
    public String getCityName();

    public OdCity getCity();

    void setCityName(String cityName);

    void setStreetName(String streetName);

    void setPostcode(String postcode);

    void setHouseNumber(Integer houseNumber);

    void setFullHouseNumber(String fullHouseNumber);

    void setHouseLetter(Character houseLetter);

    void setHouseNumberExtra(String houseNumberExtra);

    void setStreet(OdStreet street);
}
