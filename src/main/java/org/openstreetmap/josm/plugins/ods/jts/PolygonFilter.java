package org.openstreetmap.josm.plugins.ods.jts;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.prep.PreparedPolygon;

/**
 * Filter a Josm dataSet using a Polygon.
 * All primitives that are completely outside the dataSet will
 * be removed from the dataSet
 * 
 * @author gertjan
 *
 */
public class PolygonFilter {
    private final static GeoUtil geoUtil = GeoUtil.getInstance();

    private final PreparedPolygon pp;
    private final Set<OsmPrimitive> keep = new HashSet<>();

    public PolygonFilter(Polygon polygon) {
        this.pp = new PreparedPolygon(polygon);
    }
    
    public void filter(DataSet dataSet) {
        // Create a Collection of all primitives we should keep
        for (Node node : dataSet.getNodes()) {
            if (node.isIncomplete()) {
                continue;
            }
            Point point = geoUtil.toPoint(node);
            if (pp.contains(point)) {
                keep.add(node);
                for (OsmPrimitive primitive : node.getReferrers()) {
                    keep(primitive);
                }
            }
        }
        // Remove all primitives that are not in the Collection of primitives to keep
        Iterator<OsmPrimitive> it = dataSet.allPrimitives().iterator();
        while (it.hasNext()) {
            OsmPrimitive primitive = it.next();
            if (!keep.contains(primitive)) {
                dataSet.removePrimitive(primitive.getPrimitiveId());
            }
        }
    }
    
    private void keep(OsmPrimitive primitive) {
        switch(primitive.getType()) {
        case NODE:
            keep.add(primitive);
            break;
        case WAY:
            Way way = (Way) primitive;
            keep.add(way);
            keep.addAll(way.getNodes());
            break;
        case RELATION:
            Relation relation = (Relation)primitive;
            keep.add(relation);
            for (OsmPrimitive p : relation.getMemberPrimitives()) {
                keep(p);
            }
        default:
            break;
        
        }
    }
}
