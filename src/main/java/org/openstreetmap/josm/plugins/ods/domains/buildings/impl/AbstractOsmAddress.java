package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import org.openstreetmap.josm.plugins.ods.domains.buildings.HouseNumber;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddress;
import org.openstreetmap.josm.plugins.ods.domains.places.OsmCity;
import org.openstreetmap.josm.plugins.ods.domains.streets.OsmStreet;

public class AbstractOsmAddress implements OsmAddress {
    private HouseNumber houseNumber;
    private String postcode;
    private String streetName;
    private OsmStreet street;
    private String cityName;
    private OsmCity city;

    @Override
    public HouseNumber getHouseNumber() {
        return houseNumber;
    }

    @Override
    public void setHouseNumber(HouseNumber houseNumber) {
        this.houseNumber = houseNumber;
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
    public OsmStreet getStreet() {
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
    public String getCityName() {
        return cityName;
    }

    @Override
    public OsmCity getCity() {
        return city;
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
