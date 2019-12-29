package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;

public abstract class AbstractOdAddress implements OdAddress {
//    private HouseNumber houseNumber;
    private String postcode;
    private String streetName;
    private String cityName;

    @Override
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

//    @Override
//    public void setHouseNumber(HouseNumber houseNumber) {
//        this.houseNumber = houseNumber;
//    }
//    
//    @Override
//    public HouseNumber getHouseNumber() {
//        return houseNumber;
//    }

    @Override
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    @Override
    public String getStreetName() {
        return streetName;
    }

    @Override
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Override
    public String getPostcode() {
        return postcode;
    }

    @Override
    public String getCityName() {
        return cityName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getStreetName()).append(" ");
        sb.append(getHouseNumber());
        if (getPostcode() != null) {
            sb.append(' ').append(getPostcode());
        }
        if (getCityName() != null) {
            sb.append(' ').append(getCityName());
        }
        return sb.toString();
    }
}
