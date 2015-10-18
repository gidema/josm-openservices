package org.openstreetmap.josm.plugins.ods.matching;

import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.managers.BuildingManager;
import org.openstreetmap.josm.plugins.ods.tasks.Task;

@Deprecated
public class BuildingMatcher implements Task {
    private final BuildingManager manager;

    public BuildingMatcher(BuildingManager buildingManager) {
        super();
        this.manager = buildingManager;
    }

    @Override
    public void run(Context ctx) {
        for (Building building : manager.getForeignBuildings()) {
            processGtBuilding(building);
        }
        for (Building building : manager.getOsmBuildings()) {
            processOsmBuilding(building);
        }
    }

    private void processGtBuilding(Building gtBuilding) {
        Object id = gtBuilding.getReferenceId();
        if (manager.getBuildingMatches().containsKey(id)) {
            return;
        }
        Building osmBuilding = manager.getOsmBuildings().get(id);
        if (osmBuilding != null) {
            BuildingMatch match = new BuildingMatch(osmBuilding, gtBuilding);
            manager.getBuildingMatches().put(id, match);
            manager.getOsmBuildings().remove(id);
        } else {
            manager.getForeignBuildings().put(id, gtBuilding);
        }
    }

    private void processOsmBuilding(Building osmBuilding) {
        Object id = osmBuilding.getReferenceId();
        if (id == null) {
            manager.getUnidentifiedBuildings().add(osmBuilding);
            return;
        }
        Building gtBuilding = manager.getForeignBuildings().get(id);
        if (gtBuilding != null) {
            BuildingMatch match = new BuildingMatch(osmBuilding, gtBuilding);
            manager.getBuildingMatches().put(id, match);
            manager.getForeignBuildings().remove(id);
        } else {
            manager.getOsmBuildings().put(id, osmBuilding);
        }
    }
}
