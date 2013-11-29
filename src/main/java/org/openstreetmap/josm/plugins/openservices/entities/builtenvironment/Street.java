package org.openstreetmap.josm.plugins.openservices.entities.builtenvironment;

import java.util.Set;

import org.openstreetmap.josm.plugins.openservices.entities.Entity;

public interface Street extends Entity {
    String NAMESPACE = "ods.street".intern();

    public Place getPlace();

    public String getName();

    public Set<Address> getAddresses();

    public String getCityName();

    public String getStreetName();
}
