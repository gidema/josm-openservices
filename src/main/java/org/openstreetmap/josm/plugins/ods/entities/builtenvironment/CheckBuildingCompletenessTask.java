package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.foreign.OpenDataBuildingStore;
import org.openstreetmap.josm.plugins.ods.tasks.Task;

import com.vividsolutions.jts.geom.Polygonal;
import com.vividsolutions.jts.geom.prep.PreparedPolygon;

public class CheckBuildingCompletenessTask implements Task {
    private final OpenDataBuildingStore buildingStore;
    
    public CheckBuildingCompletenessTask(OpenDataBuildingStore buildingStore) {
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
