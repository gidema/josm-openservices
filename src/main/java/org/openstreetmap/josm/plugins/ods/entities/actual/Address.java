package org.openstreetmap.josm.plugins.ods.entities.actual;

public interface Address extends Comparable<Address> {
    public Integer getHouseNumber();
    
    public String getFullHouseNumber();

    public String getHouseName();

    public String getStreetName();

    public Street getStreet();

    public String getPostcode();

    public String getCityName();

    public City getCity();
}
