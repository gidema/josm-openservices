package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.Set;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface Street extends Entity {
    String TYPE = "ods:street";

    public City getCity();

    public String getName();

    public Set<Address> getAddresses();

    public String getCityName();

    public String getStreetName();
}
