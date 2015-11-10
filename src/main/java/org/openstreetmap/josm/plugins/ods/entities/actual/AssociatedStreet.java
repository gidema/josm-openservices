package org.openstreetmap.josm.plugins.ods.entities.actual;

import java.util.Collection;

import org.openstreetmap.josm.data.osm.Relation;

public interface AssociatedStreet {
    public Relation getOsmPrimitive();
    public String getName();
    public Collection<Building> getBuildings();
    public Collection<AddressNode> getAddressNodes();
    public Collection<Street> getStreets();
}
