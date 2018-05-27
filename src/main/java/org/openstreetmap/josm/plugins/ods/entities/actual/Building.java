package org.openstreetmap.josm.plugins.ods.entities.actual;

import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingType;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.matching.BuildingMatch;

public interface Building extends Entity {
    /**
     * Return the address information associated with this building.
     *
     * @return null if no address is associated with the building
     */

    /**
     * Check is the full area of this building has been loaded. This is true if
     * the building is completely covered by the downloaded area.
     *
     * @return
     */

    public String getStartDate();

    public BuildingType getBuildingType();

    public void setMatch(BuildingMatch match);

    @Override
    public BuildingMatch getMatch();

    // Setters
    public void setBuildingType(BuildingType buildingType);
}
