package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Unknown;

import org.locationtech.jts.geom.Point;
import org.openstreetmap.josm.plugins.ods.domains.buildings.HouseNumber;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOdEntity;
import org.openstreetmap.josm.plugins.ods.matching.AddressNodeMatch;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

public class AbstractOdAddressNode extends AbstractOdEntity implements OdAddressNode {
    private OdAddress address;
    private Object buildingRef;
    private OdBuilding building;
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
    public HouseNumber getHouseNumber() {
        return address.getHouseNumber();
    }

    @Override
    public String getStreetName() {
        return address.getStreetName();
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
    public Completeness getCompleteness() {
        return building == null ? Unknown : building.getCompleteness();
    }

    @Override
    public Object getBuildingRef() {
        return buildingRef;
    }

    public void setBuildingRef(Object buildingRef) {
        this.buildingRef = buildingRef;
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
