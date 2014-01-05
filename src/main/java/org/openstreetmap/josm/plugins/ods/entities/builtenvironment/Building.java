package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.Set;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.Geometry;

public interface Building extends Entity {
    public void setGeometry(Geometry geometry);
	public Geometry getGeometry();
	public City getCity();
    public Set<AddressNode> getAddresses();
    public void setBlock(Block block);
	public Block getBlock();
	public Set<Building> getNeighbours();
	public void addNeighbour(Building building);

	
	/**
	 * Check is the full area of this building has been loaded.
	 * This is true if the building is completely covered by the
	 * downloaded area.
	 * 
	 * @return
	 */
    public void setIncomplete(boolean incomplete);

	public boolean isUnderConstruction();
	
	public String getStartDate();
}
