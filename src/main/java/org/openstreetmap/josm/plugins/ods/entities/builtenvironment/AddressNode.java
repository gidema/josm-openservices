package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.Point;

public interface AddressNode extends Entity {
	public final static String TYPE = "ods:address";

    public Address getAddress();

	public Block getBlock();
	
	public Object getBuildingRef();

    public void setBuilding(Building building);

    public Building getBuilding();

	public Point getGeometry();
}
