package org.openstreetmap.josm.plugins.ods.domains.streets;

import java.util.Set;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.domains.places.OdCity;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;

public interface OdStreet extends OdEntity {
    public OdCity getCity();

    public String getName();

    public Set<OdAddress> getAddresses();

    public String getCityName();

    public String getStreetName();
}
