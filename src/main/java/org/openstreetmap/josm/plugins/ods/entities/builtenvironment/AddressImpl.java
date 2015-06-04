package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.apache.commons.lang.ObjectUtils;

public class AddressImpl implements MutableAddress {
    private Integer houseNumber;
    private String fullHouseNumber;
    private String postcode;
    private String houseName;
    private String streetName;
    private Street street;
    private String cityName;
    private City city;

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
    public void setHouseNumber(Integer houseNumber) {
        this.houseNumber = houseNumber;
    }
    
    @Override
    public void setFullHouseNumber(String fullHouseNumber) {
        this.fullHouseNumber = fullHouseNumber;
        this.parseHouseNumber();
    }
    
    @Override
    public Integer getHouseNumber() {
        return houseNumber;
    }

    @Override
    public String getFullHouseNumber() {
        if (fullHouseNumber == null) {
            fullHouseNumber = formatHouseNumber();
        }
        return fullHouseNumber;
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
    public City getCity() {
        return city;
    }
    

    @Override
    public void setStreet(Street street) {
        this.street = street;
        this.setStreetName(street.getName());
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
            result = ObjectUtils.compare(getHouseName(), a.getFullHouseNumber());
        };
        return result;
    }

    public void parseHouseNumber() {
        // Override if required
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getStreetName()).append(" ");
        sb.append(getHouseNumber()).append(" ");
        sb.append(getPostcode()).append(" ");
        sb.append(getCityName()).append(" ");       
        return sb.toString();
    }
}
