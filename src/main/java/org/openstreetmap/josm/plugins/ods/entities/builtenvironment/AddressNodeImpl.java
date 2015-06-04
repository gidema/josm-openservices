package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;

import com.vividsolutions.jts.geom.Point;

public abstract class AddressNodeImpl extends AbstractEntity implements MutableAddressNode {
    private Address address;
    private Object buildingRef;
    private Building building;
    
    public AddressNodeImpl() {
        super();
    }
    
    @Override
    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public Integer getHouseNumber() {
        return address.getHouseNumber();
    }

    @Override
    public String getFullHouseNumber() {
        return address.getFullHouseNumber();
    }

    @Override
    public String getHouseName() {
        return address.getHouseName();
    }

    @Override
    public String getStreetName() {
        return address.getStreetName();
    }

    @Override
    public Street getStreet() {
        return address.getStreet();
    }

    @Override
    public String getPostcode() {
        return address.getPostcode();
    }

    @Override
    public String getCityName() {
        return address.getCityName();
    }

    @Override
    public City getCity() {
        return address.getCity();
    }

    @Override
    public boolean isIncomplete() {
        return building == null || building.isIncomplete();
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    @Override
    public Object getBuildingRef() {
        return buildingRef;
    }
    
    @Override
    public void setBuildingRef(Object buildingRef) {
        this.buildingRef = buildingRef;
    }

    @Override
    public void setBuilding(Building building) {
        this.building = building;
    }

    @Override
    public Building getBuilding() {
        return building;
    }

    @Override
    public void setGeometry(Point point) {
        super.setGeometry(point);
    }

    @Override
    public Point getGeometry() {
        return (Point) super.getGeometry();
    }
}
