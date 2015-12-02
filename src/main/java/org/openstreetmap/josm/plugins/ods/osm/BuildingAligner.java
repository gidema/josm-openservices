package org.openstreetmap.josm.plugins.ods.osm;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.CrossingBuildingFixer;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

import com.vividsolutions.jts.geom.Geometry;


/**
 * This tasks verifies if there are adjacent buildings in
 * the down loaded data. If so, the shared nodes will be aligned.
 * 
 * TODO consider running over all buildings, not just the new ones.
 * 
 * @author gertjan
 *
 */
public class BuildingAligner {
    private final EntityStore<Building> buildingStore;
    private final Double tolerance;
    private final CrossingBuildingFixer fixer;
    
    @Deprecated
    public BuildingAligner(EntityStore<Building> buildingStore, GeoUtil geoUtil, Double tolerance) {
        super();
        this.buildingStore = buildingStore;
        this.tolerance = tolerance;
        this.fixer = new CrossingBuildingFixer(geoUtil, tolerance);
    }

    public BuildingAligner(OdsModule module, EntityStore<Building> buildingStore) {
        this.buildingStore = buildingStore;
        this.tolerance = module.getTolerance();
        this.fixer = new CrossingBuildingFixer(module.getGeoUtil(), tolerance);
    }

    public void align(Building building) {
        try {
            for (Building candidate : buildingStore.getGeoIndex().intersection(building.getGeometry())) {
                if (candidate == building) continue;
                if (building.getNeighbours().contains(candidate)) continue;
                building.getNeighbours().add(candidate);
                candidate.getNeighbours().add(building);
                analyzeCrossing(building, candidate);
            }
        }
        catch (Exception e) {
            Main.warn(e);
        }
    }
    
    private void analyzeCrossing(Building building1, Building building2) {
        Geometry geom1 = building1.getGeometry();
        Geometry geom2 = building2.getGeometry();
        if (geom1.isWithinDistance(geom2, tolerance)) {
            fixer.setBuildings(building1, building2);
            fixer.fix();
        }
    }
}
