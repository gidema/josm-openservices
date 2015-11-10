package org.openstreetmap.josm.plugins.ods.entities.actual;

public interface MutableAddressNode extends AddressNode {

    void setAddress(Address address);
    void setBuildingRef(Object buildingRef);
}
