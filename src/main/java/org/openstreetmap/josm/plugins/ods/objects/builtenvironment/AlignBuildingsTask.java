package org.openstreetmap.josm.plugins.ods.objects.builtenvironment;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.entities.EntitySource;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.tasks.Task;


/**
 * This tasks verifies if there are adjacent buildings in
 * the down loaded data. If so, the shared nodes will be aligned.
 * 
 * TODO consider running over all buildings, not just the new ones.
 * 
 * @author gertjan
 *
 */
public class AlignBuildingsTask implements Task {
    private final EntityStore<Building> buildingStore;
    private BuildingAligner buildingAligner;
    
    public AlignBuildingsTask(EntityStore<Building> buildingStore, Double tolerance) {
        super();
        this.buildingStore = buildingStore;
        this.buildingAligner = new BuildingAligner(tolerance, false);
    }

    @Override
    public void run(Context ctx) {
        EntitySource entitySource = (EntitySource) ctx.get("entitySource");
//        dataSet.beginUpdate();
        for (Building building : buildingStore) {
            long start = System.currentTimeMillis();
            if (entitySource == building.getEntitySource()) {
                for (Building neighbour : building.getNeighbours()) {
                    long start2 = System.currentTimeMillis();
                    buildingAligner.align(building,  neighbour);
                    Main.info("  Align neighbour {0}: {1} ms", neighbour.getReferenceId(), System.currentTimeMillis() - start2);
                }
            }
            Main.info("Align building {0}: {1} ms", building.getReferenceId(), System.currentTimeMillis() - start);
        }
//        dataSet.endUpdate();
    }
}
