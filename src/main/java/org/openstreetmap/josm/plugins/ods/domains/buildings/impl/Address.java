package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

public interface Address {
    public Integer getHouseNumber();

    public Character getHouseLetter();

    public String getHouseNumberExtra();

    public String getFullHouseNumber();

    public String getStreetName();

    public String getPostcode();

    public String getCityName();
}
