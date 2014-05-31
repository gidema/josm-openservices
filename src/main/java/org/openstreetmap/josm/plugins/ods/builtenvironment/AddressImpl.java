package org.openstreetmap.josm.plugins.ods.builtenvironment;

import org.apache.commons.lang.ObjectUtils;

public class AddressImpl implements Address {
    private String postcode;
    private String houseNumber;
    private String houseName;
    private String streetName;
    private Street street;
    private String cityName;
    private City city;

    @Override
    public City getCity() {
        return city;
    }
    
    @Override
    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    @Override
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }
    
    @Override
    public String getStreetName() {
        return streetName;
    }

    @Override
    public Street getStreet() {
        return street;
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
    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        this.parseHouseNumber();
    }
    
    @Override
    public String getHouseNumber() {
        if (houseNumber == null) {
            houseNumber = formatHouseNumber();
        }
        return houseNumber;
    }

    @Override
    public String getHouseName() {
        return houseName;
    }

    @Override
    public String getCityName() {
        return cityName;
    }

    @Override
    public void setStreet(Street street) {
        this.street = street;
        this.streetName = street.getName();
    }
    
    public String formatHouseNumber() {
        return null;
    }

    @Override
    public int compareTo(Address a) {
        int result = ObjectUtils.compare(getCityName(), a.getCityName());
        if (result == 0) {
            result = ObjectUtils.compare(getPostcode(), a.getPostcode());
        };
        if (result == 0) {
            result = ObjectUtils.compare(getStreetName(), a.getStreetName());
        };
        if (result == 0) {
            result = ObjectUtils.compare(getHouseName(), a.getHouseName());
        };
        return result;
    }

    public void parseHouseNumber() {
        // Override if required
    }
}
