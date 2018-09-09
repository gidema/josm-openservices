package org.openstreetmap.josm.plugins.ods.domains.buildings;

import org.openstreetmap.josm.plugins.ods.domains.buildings.matching.BuildingMatch;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface Building extends Entity {
    public String getStartDate();

    public BuildingType getBuildingType();

    public BuildingStatus getStatus();

    @Override
    public BuildingMatch getMatch();

    // Setters
    public void setMatch(BuildingMatch match);

    public void setBuildingType(BuildingType buildingType);
}
