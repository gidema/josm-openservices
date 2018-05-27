package org.openstreetmap.josm.plugins.ods.domains.buildings;

import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.Address;
import org.openstreetmap.josm.plugins.ods.domains.places.OsmCity;
import org.openstreetmap.josm.plugins.ods.domains.streets.OsmStreet;

public interface OsmAddress extends Address {

    public OsmStreet getStreet();

    public OsmCity getCity();

    void setCityName(String cityName);

    void setStreetName(String streetName);

    void setPostcode(String postcode);

    void setHouseNumber(Integer houseNumber);

    void setFullHouseNumber(String fullHouseNumber);

    void setHouseLetter(Character houseLetter);

    void setHouseNumberExtra(String houseNumberExtra);

    void setStreet(OsmStreet street);
}
