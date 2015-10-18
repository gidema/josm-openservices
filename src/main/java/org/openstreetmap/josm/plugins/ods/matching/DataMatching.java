package org.openstreetmap.josm.plugins.ods.matching;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.foreign.OpenDataBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.osm.OsmBuildingStore;

@Deprecated
public class DataMatching {
    private OpenDataBuildingStore gtBuildingStore;
    private OsmBuildingStore osmBuildingStore;
    private Map<Object, BuildingMatch> matches = new HashMap<>();
    private Map<Object, Building> foreignBuildings = new HashMap<>();
    private Map<Object, Building> osmBuildings = new HashMap<>();
    private Set<Building> unIdentifiedBuildings = new HashSet<>();

    public OpenDataBuildingStore getOdBuildingStore() {
        return gtBuildingStore;
    }

    public OsmBuildingStore getOsmBuildingStore() {
        return osmBuildingStore;
    }

    public Map<Object, BuildingMatch> getBuildingMatches() {
        return matches;
    }

    public Map<Object, Building> getForeignBuildings() {
        return foreignBuildings;
    }

    public Map<Object, Building> getOsmBuildings() {
        return osmBuildings;
    }

    public Set<Building> getUnIdentifiedBuildings() {
        return unIdentifiedBuildings;
    }
}
