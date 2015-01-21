package org.openstreetmap.josm.plugins.ods.objects.builtenvironment;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.entities.EntitySource;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.osm.SmallSegmentRemover;
import org.openstreetmap.josm.plugins.ods.tasks.Task;


/**
 * This tasks verifies if the building way(s) contain segments shorter than
 * the given tolerance. These short segments will be removed to prevent errors
 * when aligning the building to another building.
 * TODO There are 2 issues with this approach
 * 1. If a node is close to, but not yet connected to a neighboring building.
 *   This algorithm might choose the wrong coordinates for the new point.
 * 2. TODO what was the other issue again?
 * Anyway, we might consider to move this functionality into the building aligner.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class SimplifyBuildingSegmentsTask implements Task {
    private final EntityStore<Building> buildingStore;
    private final double tolerance;
    
    public SimplifyBuildingSegmentsTask(EntityStore<Building> buildingStore, Double tolerance) {
        super();
        this.buildingStore = buildingStore;
        this.tolerance = tolerance;
    }

    @Override
    public void run(Context ctx) {
        EntitySource entitySource = (EntitySource) ctx.get("entitySource");
        for (Building building : buildingStore) {
            // TODO Should run over incomplete buildings as well
            if (entitySource == building.getEntitySource() && !building.isIncomplete()) {
                OsmPrimitive osm = building.getPrimitive();
                if (osm.getType() == OsmPrimitiveType.WAY) {
                    simplifyWay((Way)osm);
                }
                else if (osm.getType() == OsmPrimitiveType.RELATION) {
                    simplifyRelation((Relation)osm);
                }
            }
        }
    }
    
    
    private void simplifyRelation(Relation relation) {
        for (Way way : relation.getMemberPrimitives(Way.class)) {
            simplifyWay(way);
        }
    }

    private void simplifyWay(Way way) {
        SmallSegmentRemover.removeSmallSegments(way, tolerance, false);
    }
}
