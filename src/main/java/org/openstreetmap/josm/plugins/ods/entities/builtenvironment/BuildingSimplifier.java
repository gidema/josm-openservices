package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.Iterator;

import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.tasks.Task;
import org.openstreetmap.josm.tools.I18n;

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
public class BuildingSimplifier implements Task {
    private Double tolerance;
    private GtBuildingStore buildingStore;
    
    public BuildingSimplifier(GtBuildingStore buildingStore, Double tolerance) {
        super();
        this.buildingStore = buildingStore;
        this.tolerance = tolerance;
    }

    public void run(Context ctx) {
        Iterator<Building> buildings = buildingStore.iterator();
        while (buildings.hasNext()) {
            Building building = buildings.next();
            Geometry geom = building.getGeometry();
            geom = DouglasPeuckerSimplifier.simplify(
                    building.getGeometry(), tolerance);
            if (!geom.isValid()) {
                geom = TopologyPreservingSimplifier.simplify(geom, tolerance);
            }
            if (geom.isValid()) {
                building.setGeometry(geom);
            }
            else {
                System.out.println(I18n.tr("Could not simply building {0}", building.getReferenceId()));
            }
        }
    }
}
