package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

public interface MutableAddressNode extends AddressNode {

    void setAddress(Address address);
    void setBuildingRef(Object buildingRef);
}
