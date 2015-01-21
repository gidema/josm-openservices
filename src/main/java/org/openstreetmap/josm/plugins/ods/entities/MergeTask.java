package org.openstreetmap.josm.plugins.ods.entities;

import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.tasks.Task;

@Deprecated
public class MergeTask<T extends Entity> implements Task {
    private final EntityStore<T> newEntityStore;
    private final EntityStore<T> allEntityStore;
    
    public MergeTask(EntityStore<T> newEntityStore,
            EntityStore<T> allEntityStore) {
        super();
        this.newEntityStore = newEntityStore;
        this.allEntityStore = allEntityStore;
    }


    @Override
    public void run(Context ctx) {
        allEntityStore.extendBoundary(newEntityStore.getBoundary());
        for (T entity : newEntityStore) {
            if (allEntityStore.getBoundary().covers(entity.getGeometry())) {
                allEntityStore.add(entity);
            }
        }
    }
}
