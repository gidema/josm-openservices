package org.openstreetmap.josm.plugins.ods.builtenvironment;

import java.util.Iterator;

import org.openstreetmap.josm.plugins.ods.DataLayer;
import org.openstreetmap.josm.plugins.ods.analysis.Analyzer;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;


/**
 * This analyzer verifies if the whole building area, and therefore
 * all address nodes in the building, have been downloaded.
 * It sets the incomplete flag in the building accordingly. 
 * 
 * @author gertjan
 *
 */
public class BuildingCompletenessAnalyzer implements Analyzer {
    
    public void analyze(DataLayer dataLayer, EntitySet newEntities) {
        BuiltEnvironment entitySet = new BuiltEnvironment(newEntities);
        Iterator<Building> it = entitySet.getBuildings().iterator();
        while (it.hasNext()) {
            Building building = it.next();
            boolean incomplete = !entitySet.getBoundary().covers(building.getGeometry());
            building.setIncomplete(incomplete);
        }
    }
}
