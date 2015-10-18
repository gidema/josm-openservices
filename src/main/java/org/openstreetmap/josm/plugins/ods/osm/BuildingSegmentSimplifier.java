package org.openstreetmap.josm.plugins.ods.osm;

import java.util.function.Consumer;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;


/**
 * This tasks verifies if the building way(s) contain segments shorter than
 * the given tolerance. These short segments will be removed to prevent errors
 * when aligning the building to another building.
 * TODO There are 2 issues with this approach
 * 1. If a node is close to, but not yet connected to a neighboring building.
 *   This algorithm might choose the wrong coordinates for the new point.
 * Anyway, we might consider to move this functionality into the building aligner.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BuildingSegmentSimplifier implements Consumer<Building> {
    private final double tolerance;
    
    public BuildingSegmentSimplifier(Double tolerance) {
        super();
        this.tolerance = tolerance;
    }

    @Override
    public void accept(Building building) {
    // TODO Should run over incomplete buildings as well
        OsmPrimitive osm = building.getPrimitive();
        if (osm.getType() == OsmPrimitiveType.WAY) {
            simplifyWay((Way)osm);
        }
        else if (osm.getType() == OsmPrimitiveType.RELATION) {
            simplifyRelation((Relation)osm);
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
