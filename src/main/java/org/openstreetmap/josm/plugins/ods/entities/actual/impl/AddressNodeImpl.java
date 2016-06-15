package org.openstreetmap.josm.plugins.ods.entities.actual.impl;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.plugins.ods.entities.actual.Address;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.City;
import org.openstreetmap.josm.plugins.ods.entities.actual.MutableAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Street;
import org.openstreetmap.josm.tools.I18n;

import com.vividsolutions.jts.geom.Point;

public abstract class AddressNodeImpl extends AbstractEntity implements MutableAddressNode {
    private Address address;
    private Object buildingRef;
    private Building building;
    
    public AddressNodeImpl() {
        super();
    }
    
    @Override
    public EntityType<AddressNode> getEntityType() {
        return AddressNodeEntityType.getInstance();
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
    public Character getHouseLetter() {
        return address.getHouseLetter();
    }

    @Override
    public String getHouseNumberExtra() {
        return address.getHouseNumberExtra();
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
        return building != null && building.isIncomplete();
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
        try {
            return (Point) super.getGeometry();
        }
        catch (ClassCastException e) {
            Main.warn(I18n.tr("The geometry of {0} is not a point", toString()));
            return super.getGeometry().getCentroid();
        }
    }
    
    public String toString() {
        return getAddress().toString();
    }
}
