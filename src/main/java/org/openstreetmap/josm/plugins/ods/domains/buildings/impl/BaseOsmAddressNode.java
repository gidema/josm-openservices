package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.matching.AddressNodeMatch;
import org.openstreetmap.josm.plugins.ods.domains.places.OsmCity;
import org.openstreetmap.josm.plugins.ods.domains.streets.OsmStreet;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOsmEntity;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

import com.vividsolutions.jts.geom.Point;

public class BaseOsmAddressNode extends AbstractOsmEntity implements OsmAddressNode {
    private OsmAddress address;
    private OsmBuilding building;
    private AddressNodeMatch match;

    public BaseOsmAddressNode() {
        super();
    }

    @Override
    public void setAddress(OsmAddress address) {
        this.address = address;
    }

    @Override
    public OsmAddress getAddress() {
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

    public OsmStreet getStreet() {
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

    public OsmCity getCity() {
        return address.getCity();
    }

    @Override
    public void setBuilding(OsmBuilding building) {
        this.building = building;
    }

    @Override
    public OsmBuilding getBuilding() {
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
}
