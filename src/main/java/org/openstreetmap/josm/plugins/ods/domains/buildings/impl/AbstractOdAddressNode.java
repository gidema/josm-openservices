package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Unknown;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuildingUnit;
import org.openstreetmap.josm.plugins.ods.domains.buildings.matching.AddressNodeMatch;
import org.openstreetmap.josm.plugins.ods.domains.places.OdCity;
import org.openstreetmap.josm.plugins.ods.domains.streets.OdStreet;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOdEntity;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

import com.vividsolutions.jts.geom.Point;

public class AbstractOdAddressNode extends AbstractOdEntity implements OdAddressNode {
    private OdAddress address;
    private Long addressNodeId;
    private OdBuilding building;
    private OdBuildingUnit buildingUnit;
    private AddressNodeMatch match;

    public AbstractOdAddressNode() {
        super();
    }

    @Override
    public void setAddress(OdAddress address) {
        this.address = address;
    }

    @Override
    public OdAddress getAddress() {
        return address;
    }

    @Override
    public Long getPrimaryId() {
        return addressNodeId != null ? addressNodeId : - buildingUnit.getBuildingUnitId();
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
    public String getStreetName() {
        return address.getStreetName();
    }

    public OdStreet getStreet() {
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

    public OdCity getCity() {
        return address.getCity();
    }

    @Override
    public Completeness getCompleteness() {
        return building == null ? Unknown : building.getCompleteness();
    }

    @Override
    public void setBuilding(OdBuilding building) {
        this.building = building;
    }

    @Override
    public OdBuilding getBuilding() {
        return building;
    }

    @Override
    public OdBuildingUnit getBuildingUnit() {
        return this.buildingUnit;
    }

    @Override
    public void setBuildinUnit(OdBuildingUnit buildingUnit) {
        this.buildingUnit = buildingUnit;
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
            Logging.warn(I18n.tr("The geometry of {0} is not a point", toString()));
            return super.getGeometry().getCentroid();
        }
    }

    @Override
    public AddressNodeMatch getMatch() {
        return match;
    }

    @Override
    public void setMatch(AddressNodeMatch match) {
        this.match = match;
    }

    @Override
    public String toString() {
        return getAddress().toString();
    }

    //    @Override
    //    public int compareTo(OdAddress o) {
    //        // TODO Auto-generated method stub
    //        return 0;
    //    }
}
