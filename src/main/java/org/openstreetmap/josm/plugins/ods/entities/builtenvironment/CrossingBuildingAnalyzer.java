package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.Iterator;

import org.openstreetmap.josm.plugins.ods.DataLayer;
import org.openstreetmap.josm.plugins.ods.analysis.Analyzer;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;

import com.vividsolutions.jts.geom.Geometry;


/**
 * This analyzer verifies if there are overlapping buildings in
 * the downloaded data.
 * 
 * TODO consider running over all buildings, not just the new ones.
 * 
 * @author gertjan
 *
 */
public class CrossingBuildingAnalyzer implements Analyzer, BlockAnalyzer {
    private final Double tolerance;
    private final CrossingBuildingFixer fixer;
    
    public CrossingBuildingAnalyzer(Double tolerance) {
        super();
        this.tolerance = tolerance;
        this.fixer = new CrossingBuildingFixer(tolerance);
    }

    @Override
    public  void analyze(Block block) {
        for (Building building : block.getExternalBuildings()) {
            Comparable<Object> buildingId = building.getId();
            for (Building neighbour :building.getNeighbours()) {
                Comparable<Object> neighbourId = neighbour.getId();
                 if (neighbourId.compareTo(buildingId) == 1) {
                     analyzeCrossing(building, neighbour);
                 }
            }
        }
    }
    
    public void analyze(DataLayer dataLayer, EntitySet newEntities) {
        BuiltEnvironment newEnvironment = new BuiltEnvironment(newEntities);
        Iterator<Building> buildings1 = newEnvironment.getBuildings().iterator();
        while (buildings1.hasNext()) {
            Building building1 = buildings1.next();
            analyze(building1, newEnvironment);
        }
    }
    
    private void analyze(Building building1, BuiltEnvironment newEnvironment) {
        Iterator<Building> buildings2 = newEnvironment.getBuildings().iterator();
        while (buildings2.hasNext()) {
            Building building2 = buildings2.next(); 
            // No need to run each comparison twice
            if (building1.getId().toString().compareTo(building2.getId().toString()) > 0) {
                analyzeCrossing(building1, building2);
            }
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
