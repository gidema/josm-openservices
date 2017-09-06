package org.openstreetmap.josm.plugins.ods.entities.actual;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;

import com.vividsolutions.jts.geom.Point;

public interface AddressNode extends Entity, Address {

    public Address getAddress();

	public Object getBuildingRef();

    public void setBuilding(Building building);

    public Building getBuilding();

    public void setGeometry(Point point);
    
    @Override
    public Point getGeometry();
    
    @Override
    public EntityType<AddressNode> getEntityType();
}
