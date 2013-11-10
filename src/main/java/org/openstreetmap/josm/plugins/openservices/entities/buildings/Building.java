package org.openstreetmap.josm.plugins.openservices.entities.buildings;

import java.util.Set;

import org.openstreetmap.josm.plugins.openservices.entities.Entity;

import com.vividsolutions.jts.geom.Polygon;

public interface Building extends Entity {
	public final static String NAMESPACE = "ods:building".intern();

	public Polygon getGeometry();
	public Place getPlace();
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
}
