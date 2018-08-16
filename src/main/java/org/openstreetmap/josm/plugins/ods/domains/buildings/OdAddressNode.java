package org.openstreetmap.josm.plugins.ods.domains.buildings;

import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.Address;
import org.openstreetmap.josm.plugins.ods.domains.buildings.matching.AddressNodeMatch;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;

import com.vividsolutions.jts.geom.Point;

public interface OdAddressNode extends OdEntity, Address {
    @Override
    public Point getGeometry();

    @Override
    public AddressNodeMatch getMatch();

    public OdAddress getAddress();

    @Override
    public Long getPrimaryId();

    public OdBuilding getBuilding();

    public void setBuilding(OdBuilding building);

    public OdBuildingUnit getBuildingUnit();

    public void setBuildinUnit(OdBuildingUnit buildingUnit);

    public void setGeometry(Point point);

    public void setAddress(OdAddress address);

    public void setMatch(AddressNodeMatch match);

}
