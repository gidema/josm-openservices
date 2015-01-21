package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.tasks.Task;

import com.vividsolutions.jts.geom.Polygonal;
import com.vividsolutions.jts.geom.prep.PreparedPolygon;

public class CheckBuildingCompletenessTask implements Task {
    private final GtBuildingStore buildingStore;
    
    public CheckBuildingCompletenessTask(GtBuildingStore buildingStore) {
        super();
        this.buildingStore = buildingStore;
    }

    @Override
    public void run(Context ctx) {
        PreparedPolygon boundary = new PreparedPolygon((Polygonal) buildingStore.getBoundary());
        for (Building building : buildingStore) {
            if (building.isIncomplete() && boundary.covers(building.getGeometry())) {
                building.setIncomplete(false);
            }
        }
    }
}
