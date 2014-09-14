package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.tasks.Task;

/**
 * This task creates the OSM primitives and draws them on the datalayer.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class CreateBuildingPrimitivesTask implements Task {
    private GtBuildingStore buildingStore;
    private PrimitiveBuilder<Building> primitiveBuilder;

    public CreateBuildingPrimitivesTask(GtBuildingStore buildingStore,
            PrimitiveBuilder<Building> primitiveBuilder) {
        super();
        this.buildingStore = buildingStore;
        this.primitiveBuilder = primitiveBuilder;
    }


    @Override
    public void run() {
        for (Building building : buildingStore) {
            if (!building.isIncomplete()) {
                primitiveBuilder.createPrimitives(building);
            }
        }
    }

}
