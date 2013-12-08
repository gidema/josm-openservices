package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.io.Serializable;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.Point;

public interface Address extends Entity {
	public final static String TYPE = "ods:address";

	public City getCity();

	public Block getBlock();
	
	public Serializable getBuildingRef();

	public Building getBuilding();

    public String getStreetName();

    public Street getStreet();

	public String getPostcode();

	public String getHouseNumber();

	public String getHouseName();
	
	public Point getGeometry();

    String getPlaceName();
}
