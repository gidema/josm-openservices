package org.openstreetmap.josm.plugins.ods.entities.actual;

import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.plugins.ods.matching.BuildingMatch;

import com.vividsolutions.jts.geom.Geometry;

public interface Building extends Entity {
    @Override
    public Geometry getGeometry();

    public City getCity();

    /**
     * Return the address information associated with this building.
     * 
     * @return null if no address is associated with the building
     */
    public Address getAddress();

    /**
     * Return the address nodes associated with this building.
     * 
     * @return empty collection if no address nodes are associated with this
     *         building.
     */
    public List<AddressNode> getAddressNodes();

    public Set<Building> getNeighbours();

    /**
     * Check is the full area of this building has been loaded. This is true if
     * the building is completely covered by the downloaded area.
     * 
     * @return
     */

    public void setStartDate(String string);
    
    public String getStartDate();

    public BuildingType getBuildingType();

    @Override
    public BuildingMatch getMatch();

    @Override
    public EntityType<Building> getEntityType();
    
    // Setters
    public void setBuildingType(BuildingType buildingType);

    public void setIncomplete(boolean incomplete);
}
