package org.openstreetmap.josm.plugins.ods.matching;

import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.tasks.Task;

/**
 * 
 * Building matcher that uses the id indexes to try to match buildings by id.
 * TODO Implement
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
@Deprecated
public class BuildingMatcherNg implements Task {
    private final DataMatching matching;
//    private final ForeignBuildingStore gtBuildingStore;
//    private final OsmBuildingStore osmBuildingStore;
//        private Map<Object, BuildingMatch> matches = new HashMap<>();
//        private Map<Object, Building> gtBuildings = new HashMap<>();
//        private Map<Object, Building> osmBuildings = new HashMap<>();
//        private Set<Building> unIdentifiedBuildings = new HashSet<>();
    
        
        public BuildingMatcherNg(DataMatching dataMatching) {
            super();
            this.matching = dataMatching;
        }

//        public BuildingMatcher(ForeignBuildingStore gtBuildingStore,
//                OsmBuildingStore osmBuildingStore) {
//            super();
//            this.gtBuildingStore = gtBuildingStore;
//            this.osmBuildingStore = osmBuildingStore;
//        }
//
        @Override
        public void run(Context ctx) {
            for (Building building : matching.getOdBuildingStore()) {
                processGtBuilding(building);
            }
            for (Building building : matching.getOsmBuildingStore()) {
                processOsmBuilding(building);
            }
        }
        
        private void processGtBuilding(Building gtBuilding) {
            Object id = gtBuilding.getReferenceId();
            if (matching.getBuildingMatches().containsKey(id)) {
                return;
            }
            Building osmBuilding = matching.getOsmBuildings().get(id);
            if (osmBuilding!= null) {
                BuildingMatch match = new BuildingMatch(osmBuilding, gtBuilding);
                matching.getBuildingMatches().put(id, match);
                matching.getOsmBuildings().remove(id);
            }
            else {
                matching.getForeignBuildings().put(id, gtBuilding);
            }
        }
        
        private void processOsmBuilding(Building osmBuilding) {
            Object id = osmBuilding.getReferenceId();
            if (id == null) {
                matching.getUnIdentifiedBuildings().add(osmBuilding);
                return;
            }
            Building gtBuilding = matching.getForeignBuildings().get(id);
            if (gtBuilding != null) {
                BuildingMatch match = new BuildingMatch(osmBuilding, gtBuilding);
                matching.getBuildingMatches().put(id, match);
                matching.getForeignBuildings().remove(id);
            }
            else {
                matching.getOsmBuildings().put(id,  osmBuilding);
            }
        }
}
