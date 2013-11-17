package org.openstreetmap.josm.plugins.openservices.entities.builtenvironment;

import java.io.Serializable;

import org.openstreetmap.josm.plugins.openservices.entities.Entity;

import com.vividsolutions.jts.geom.Point;

public interface Address extends Entity {
	public final static String NAMESPACE = "ods:address".intern();

	public Place getPlace();

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
