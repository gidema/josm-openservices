package org.openstreetmap.josm.plugins.ods.osm;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.BuildingEntityType;
import org.openstreetmap.josm.tools.Geometry;
import org.openstreetmap.josm.tools.Geometry.PolygonIntersection;
import org.openstreetmap.josm.tools.Predicate;

/**
 * Find neighbours for a Building using the Osm primitive.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OsmNeighbourFinder {
    private OdsModule module;
    private Predicate<OsmPrimitive> isBuilding = BuildingEntityType.IsBuilding;
    private List<OsmPrimitive> neighbourBuildings = new LinkedList<>();
    private BuildingAligner buildingAligner;
    
    public OsmNeighbourFinder(OdsModule module) {
        super();
        this.module = module;
        this.buildingAligner = new BuildingAligner(module, 
            module.getOsmLayerManager().getEntityStore(Building.class));
    }

    
    public void findNeighbours(OsmPrimitive osm) {
        if (!isBuilding.evaluate(osm)) {
            return;
        }
        if (osm.getDisplayType().equals(OsmPrimitiveType.CLOSEDWAY)) {
            findWayNeighbourBuildings((Way)osm);
        }
    }
    
    public void findWayNeighbourBuildings(Way way1) {
        BBox bbox = extend(way1.getBBox(), module.getTolerance());
        for (Way way2 : way1.getDataSet().searchWays(bbox)) {
            if (way2.equals(way1)) {
                continue;
            }
            if (isBuilding.evaluate(way2)) {
                buildingAligner.align(way1, way2);
//                PolygonIntersection pi = Geometry.polygonIntersection(way1.getNodes(), way2.getNodes());
//                if (pi.equals(PolygonIntersection.CROSSING)) {
//                    neighbourBuildings.add(way2);
//                }
            }
            for (OsmPrimitive osm2 :way2.getReferrers()) {
                Relation relation = (Relation)osm2;
                if (isBuilding.evaluate(relation)) {
                    buildingAligner.align(way1, way1);
//                    neighbourBuildings.add(relation);
                }
            }
        }
    }

    private BBox extend(BBox bbox, Double delta) {
        return new BBox(bbox.getTopLeftLon() - delta,
            bbox.getBottomRightLat() - delta,
            bbox.getBottomRightLon() + delta,
            bbox.getTopLeftLat() + delta);
    }
}
