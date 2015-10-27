package org.openstreetmap.josm.plugins.ods.entities.managers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.opendata.OpenDataBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.osm.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.matching.BuildingMatch;

public class _BuildingManager {
    private final DataManager dataManager;
    private OsmBuildingStore osmBuildings = new OsmBuildingStore();
    private OpenDataBuildingStore openDataBuildings = new OpenDataBuildingStore();
    private List<Building> unidentifiedBuidings = new LinkedList<>();
    private Map<Long, BuildingMatch> buildingMatches = new HashMap<>();
    private OsmBuildingStore unmatchedOsmBuildings = new OsmBuildingStore();
    private OpenDataBuildingStore unmatchedOpenDataBuildings = new OpenDataBuildingStore();
    
    public BuildingManager(DataManager dataManager) {
        super();
        this.dataManager = dataManager;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }

    public OsmBuildingStore getOsmBuildings() {
        return osmBuildings;
    }

    public OpenDataBuildingStore getOpenDataBuildings() {
        return openDataBuildings;
    }

    public Map<Long, BuildingMatch> getMatches() {
        return buildingMatches;
    }

    public List<Building> getUnidentifiedBuildings() {
        return unidentifiedBuidings;
    }

    public Map<Long, BuildingMatch> getBuildingMatches() {
        return buildingMatches;
    }

    public OsmBuildingStore getUnmatchedOsmBuildings() {
        return unmatchedOsmBuildings;
    }

    public OpenDataBuildingStore getUnmatchedOpenDataBuildings() {
        return unmatchedOpenDataBuildings;
    }
    
//    private void addOpenDataBuilding(Building building) {
//        Long id = (Long) building.getReferenceId();
//        assert (id != null); // Open data buildings should always have an id
//        List<Building> existing = openDataBuildings.getById(id);
//        if (existing.size() > 0) {
//            return; // The building already exist in the dataset
//        }
//        openDataBuildings.add(building);
//        List<Building> osmBuildings = unmatchedOsmBuildings.getById(id);
//        if (osmBuildings.size() == 1) {
//            Building osmBuilding = osmBuildings.get(0);
//            BuildingMatch buildingMatch = new BuildingMatch(osmBuilding, building);
//            buildingMatches.put(id, buildingMatch);
//            unmatchedOsmBuildings.remove(osmBuilding);
//        }
//        else {
//            // There is no matching Osm building for this open data building
//            unmatchedOpenDataBuildings.add(building);
//        }
//    }
    
//    private void addOsmBuilding(Building osmBuilding) {
//        Long id = (Long) osmBuilding.getReferenceId();
//        if (id == null) {
//            getUnidentifiedBuildings().add(osmBuilding);
//            return;
//        }
//        List<Building> existing = osmBuildings.getById(id);
//        if (existing.size() > 0) {
//            return; // The building already exist in the dataset
//        }
//        osmBuildings.add(osmBuilding);
//        List<Building> openDataBuildings = unmatchedOpenDataBuildings.getById(id);
//        if (openDataBuildings.size() == 1) {
//            Building openDataBuilding = openDataBuildings.get(0);
//            BuildingMatch match = new BuildingMatch(osmBuilding, openDataBuilding);
//            buildingMatches.put(id, match);
//            unmatchedOpenDataBuildings.remove(openDataBuilding);
//        }
//        else {
//            unmatchedOsmBuildings.add(osmBuilding);
//        }
//    }

//    public Consumer<Building> getOsmBuildingConsumer() {
//        return this::addOsmBuilding;
//    }

//    public Consumer<Building> getOpenDataBuildingConsumer() {
//        return this::addOpenDataBuilding;
//    }


}
