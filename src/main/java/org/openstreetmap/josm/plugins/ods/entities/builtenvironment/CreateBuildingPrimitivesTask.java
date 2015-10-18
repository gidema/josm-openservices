package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.foreign.OpenDataBuildingStore;
import org.openstreetmap.josm.plugins.ods.tasks.Task;

/**
 * This task creates the OSM primitives and draws them on the datalayer.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class CreateBuildingPrimitivesTask implements Task {
    private OpenDataBuildingStore buildingStore;
    private PrimitiveBuilder<Building> primitiveBuilder;

    public CreateBuildingPrimitivesTask(OpenDataBuildingStore buildingStore,
            PrimitiveBuilder<Building> primitiveBuilder) {
        super();
        this.buildingStore = buildingStore;
        this.primitiveBuilder = primitiveBuilder;
    }


    @Override
    public void run(Context ctx) {
        for (Building building : buildingStore) {
            if (building.getPrimitive() == null && !building.isIncomplete()) {
                primitiveBuilder.createPrimitive(building);
            }
        }
    }

}
