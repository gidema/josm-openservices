package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.tasks.Task;

/**
 * This task creates the OSM primitives and draws them on the datalayer.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class CreatePrimitivesTask<T extends Entity> implements Task {
    private EntityStore<T> entityStore;
    private PrimitiveBuilder<T> primitiveBuilder;

    public CreatePrimitivesTask(EntityStore<T> entityStore,
            PrimitiveBuilder<T> primitiveBuilder) {
        super();
        this.entityStore = entityStore;
        this.primitiveBuilder = primitiveBuilder;
    }


    @Override
    public void run(Context ctx) {
        for (T entity : entityStore) {
            if (entity.getPrimitive() == null) {
                primitiveBuilder.createPrimitive(entity);
            }
        }
    }
}
