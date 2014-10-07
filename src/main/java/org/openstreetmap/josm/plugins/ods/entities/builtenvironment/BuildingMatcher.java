package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.tasks.Task;

public class BuildingMatcher implements Task {
    private final GtBuildingStore gtBuildingStore;
    private final OsmBuildingStore osmBuildingStore;
        private Map<Object, BuildingMatch> matches = new HashMap<>();
        private Map<Object, Building> gtBuildings = new HashMap<>();
        private Map<Object, Building> osmBuildings = new HashMap<>();
        private Set<Building> unIdentifiedBuildings = new HashSet<>();
    
        public BuildingMatcher(GtBuildingStore gtBuildingStore,
                OsmBuildingStore osmBuildingStore) {
            super();
            this.gtBuildingStore = gtBuildingStore;
            this.osmBuildingStore = osmBuildingStore;
        }

        @Override
        public void run() {
            for (Building building : gtBuildingStore) {
                processGtBuilding(building);
            }
            for (Building building : osmBuildingStore) {
                processOsmBuilding(building);
            }
        }
        
        private void processGtBuilding(Building gtBuilding) {
            Object id = gtBuilding.getReferenceId();
            if (matches.containsKey(id)) {
                return;
            }
            Building osmBuilding = osmBuildings.get(id);
            if (osmBuilding!= null) {
                BuildingMatch match = new BuildingMatch(osmBuilding, gtBuilding);
                matches.put(id, match);
                osmBuildings.remove(id);
            }
            else {
                gtBuildings.put(id, gtBuilding);
            }
        }
        
        private void processOsmBuilding(Building osmBuilding) {
            Object id = osmBuilding.getReferenceId();
            if (id == null) {
                unIdentifiedBuildings.add(osmBuilding);
                return;
            }
            Building gtBuilding = gtBuildings.get(id);
            if (gtBuilding != null) {
                BuildingMatch match = new BuildingMatch(osmBuilding, gtBuilding);
                matches.put(id, match);
                osmBuildings.remove(id);
            }
            else {
                osmBuildings.put(id,  osmBuilding);
            }
        }
        
        class BuildingMatch {
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
}
