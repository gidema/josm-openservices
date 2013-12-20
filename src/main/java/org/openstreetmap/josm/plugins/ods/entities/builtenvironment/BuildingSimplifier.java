package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.Iterator;

import org.openstreetmap.josm.plugins.ods.DataLayer;
import org.openstreetmap.josm.plugins.ods.analysis.Analyzer;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;


/**
 * This analyzer verifies if there are overlapping buildings in
 * the downloaded data.
 * 
 * TODO consider running over all buildings, not just the new ones.
 * 
 * @author gertjan
 *
 */
public class BuildingSimplifier implements Analyzer {
    private Double tolerance;
    
    public BuildingSimplifier(Double tolerance) {
        super();
        this.tolerance = tolerance;
    }

    public void analyze(DataLayer dataLayer, EntitySet newEntities) {
        BuiltEnvironment newEnvironment = new BuiltEnvironment(newEntities);
        Iterator<Building> buildings = newEnvironment.getBuildings().iterator();
        while (buildings.hasNext()) {
            Building building = buildings.next();
            Geometry geom = building.getGeometry();
            geom = DouglasPeuckerSimplifier.simplify(
                    building.getGeometry(), tolerance);
            if (!geom.isValid()) {
                geom = TopologyPreservingSimplifier.simplify(geom, tolerance);
            }
            building.setGeometry(geom);
        }
    }
}