package org.openstreetmap.josm.plugins.ods.matching;

import org.openstreetmap.josm.plugins.ods.entities.actual.Building;

public class BuildingMatch {
    Object id;
    Building osmBuilding;
    Building gtBuilding;

    public BuildingMatch(Building osmBuilding, Building gtBuilding) {
        super();
        this.osmBuilding = osmBuilding;
        this.gtBuilding = gtBuilding;
        this.id = osmBuilding.getReferenceId();
    }

    public Object getId() {
        return id;
    }

    public Building getOsmBuilding() {
        return osmBuilding;
    }

    public Building getGtBuilding() {
        return gtBuilding;
    }
}