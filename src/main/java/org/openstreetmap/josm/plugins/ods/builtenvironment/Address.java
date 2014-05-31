package org.openstreetmap.josm.plugins.ods.builtenvironment;

public interface Address extends Comparable<Address> {
    public void setHouseNumber(String houseNumber);
    public String getHouseNumber();

    public void setHouseName(String houseName);
    public String getHouseName();

    public void setStreetName(String streetName);
    public String getStreetName();

    public Street getStreet();

    public void setPostcode(String postcode);
    public String getPostcode();

    public void setCityName(String cityName);
    public String getCityName();

    public City getCity();

    public void setStreet(Street street);
}
