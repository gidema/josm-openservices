package org.openstreetmap.josm.plugins.openservices;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * The PrimitiveBuilder provides methods to create josm primitives from JTS
 * geometries and add them to a josm dataset. The JTS geometries must be in the
 * josm crs (epsg:4326) The methods take care of removal of duplicate nodes in
 * ways, and merging of nodes that refer to the same point.
 * 
 * @author Gertjan Idema
 * 
 */
public class PrimitiveBuilder {
    // private static final Long JOSM_SRID = 4326L;
    // private JTSCoordinateTransform crsTransform;
    // private final JTSCoordinateTransformFactory crsTransformFactory = new
    // Proj4jCRSTransformFactory();
    private final DataSet dataSet;

    /**
     * Create a JosmSourceManager with the specified source crs
     * 
     * @param sourceCrs
     */
    public PrimitiveBuilder(DataSet targetDataSet) {
        this.dataSet = targetDataSet;
        // try {
        // crsTransform =
        // crsTransformFactory.createJTSCoordinateTransform(sourceSRID,
        // JOSM_SRID, 10000000.0);
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
    }

    public Collection<OsmPrimitive> build(MultiPolygon polygon,
            Map<String, String> keys) {
        return Collections.singletonList(buildMultiPolygon(polygon, keys));
    }

    public Collection<OsmPrimitive> build(Point point, Map<String, String> keys) {
        OsmPrimitive node = buildNode(point, false);
        node.setKeys(keys);
        return Collections.singletonList(node);
    }

    /**
     * Create a josm Object from a MultiPolygon object The resulting Object depends
     * on whether the input polygon has inner rings. If so, the result will be a
     * Relation of type Multipolyon. Otherwise the result will be a Way
     */
    public OsmPrimitive buildMultiPolygon(MultiPolygon mpg, Map<String, String> keys) {
        OsmPrimitive primitive;
        if (mpg.getNumGeometries() > 1) {
            primitive = buildMultiPolygon(mpg);
            primitive.put("type", "multipolygon");
        } else {
            Polygon polygon = (Polygon) mpg.getGeometryN(0);
            if (polygon.getNumInteriorRing() > 0) {
                primitive = buildMultiPolygon(mpg);
                primitive.put("type", "multipolygon");
            }
            else {
                primitive = buildWay(polygon);
            }
        }
        primitive.setKeys(keys);
        return primitive;
    }

    /**
     * Create a josm MultiPolygon relation from a Polygon object.
     * 
     * @param polygon
     * @return the relation
     */
    public Relation buildMultiPolygon(Polygon polygon) {
        MultiPolygon multiPolygon = polygon.getFactory().createMultiPolygon(
                new Polygon[] { polygon });
        return buildMultiPolygon(multiPolygon);
    }

    /**
     * Create a josm MultiPolygon relation from a MultiPolygon object.
     * 
     * @param mpg
     * @return the relation
     */
    public Relation buildMultiPolygon(MultiPolygon mpg) {
        Relation relation = new Relation();
        for (int i = 0; i < mpg.getNumGeometries(); i++) {
            Polygon polygon = (Polygon) mpg.getGeometryN(i);
            Way way = buildWay(polygon.getExteriorRing());
            relation.addMember(new RelationMember("outer", way));
            for (int j = 0; j < polygon.getNumInteriorRing(); j++) {
                way = buildWay(polygon.getInteriorRingN(j));
                relation.addMember(new RelationMember("inner", way));
            }
        }
        dataSet.addPrimitive(relation);
        return relation;
    }

    /**
     * Create a josm Way from the exterior ring of a Polygon object
     * 
     * @param polygon
     * @return the way
     */
    public Way buildWay(Polygon polygon) {
        return buildWay(polygon.getExteriorRing());
    }

    /**
     * Create a josm way from a LineString object
     * 
     * @param line
     * @return
     */
    public Way buildWay(LineString line) {
        return buildWay(line.getCoordinateSequence());
    }

    private Way buildWay(CoordinateSequence points) {
        Way way = new Way();
        Node previousNode = null;
        for (int i = 0; i < points.size(); i++) {
            Node node = buildNode(points.getCoordinate(i), true);
            // Remove duplicate nodes in ways
            if (node != previousNode) {
                way.addNode(node);
            }
            previousNode = node;
        }
        dataSet.addPrimitive(way);
        return way;
    }

    /**
     * Create a josm Node from a Coordinate object. Optionally merge with
     * existing nodes.
     * 
     * @param coordinate
     * @param merge
     *            if true, merge this node with an existing node
     * @return the node
     */
    public Node buildNode(Coordinate coordinate, boolean merge) {
        LatLon latlon = new LatLon(coordinate.y, coordinate.x);
        Node node = new Node(latlon);
        if (merge) {
            BBox bbox = new BBox(node);
            List<Node> existingNodes = dataSet.searchNodes(bbox);
            if (existingNodes.size() > 0) {
                node = existingNodes.get(0);
                return node;
            }
        }
        dataSet.addPrimitive(node);
        return node;
    }

    /**
     * Create a josm Node from a Point object. Optionally merge with existing
     * nodes.
     * 
     * @param point
     * @param merge
     * @return
     */
    public Node buildNode(Point point, boolean merge) {
        if (point == null)
            return null;
        return buildNode(point.getCoordinate(), merge);
    }
}
