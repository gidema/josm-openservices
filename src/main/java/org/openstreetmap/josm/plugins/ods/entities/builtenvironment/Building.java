package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.Set;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.MultiPolygon;

public interface Building extends Entity {
	public final static String TYPE = "ods:building";

	public MultiPolygon getGeometry();
	public City getCity();
    public Set<Address> getAddresses();
	public Block getBlock();
	
	/**
	 * Check is the full area of this building has been loaded.
	 * This is true if the building is completely covered by the
	 * downloaded area.
	 * 
	 * @return
	 */
	public boolean isComplete();
	
	public boolean isUnderConstruction();
	
	public String getStartDate();
}
