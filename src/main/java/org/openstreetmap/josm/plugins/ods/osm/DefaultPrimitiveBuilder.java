package org.openstreetmap.josm.plugins.ods.osm;

import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.ODS;

/**
 * The default implementation of PrimitiveBuilder.
 * 
 * @author Gertjan Idema
 * 
 */
public class DefaultPrimitiveBuilder implements OsmPrimitiveFactory {

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#build(com.vividsolutions.jts.geom.Geometry)
     */
    @Override
    public OsmPrimitive create(DataSet dataSet, Geometry geometry, Map<String, String> tags) {
        tags.put(ODS.KEY.BASE, "true");
        switch (geometry.getGeometryType()) {
        case "Polygon":
            return create(dataSet, (Polygon)geometry, tags);
        case "MultiPolygon":
            return build(dataSet, (MultiPolygon)geometry, tags);
        case "Point":
            return build(dataSet, (Point)geometry, tags);
        case "LineString":
            return build(dataSet, (LineString)geometry, tags);
        case "MultiLineString":
            return build(dataSet, (MultiLineString)geometry, tags);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#build(com.vividsolutions.jts.geom.Polygon)
     */
    @Override
    public OsmPrimitive create(DataSet dataSet, Polygon polygon, Map<String, String> tags) {
        return buildArea(dataSet, polygon, tags);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#build(com.vividsolutions.jts.geom.MultiPolygon)
     */
    @Override
    public OsmPrimitive build(DataSet dataSet, MultiPolygon mpg, Map<String, String> tags) {
        return buildArea(dataSet, mpg, tags);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#build(com.vividsolutions.jts.geom.Point)
     */
    @Override
    public OsmPrimitive build(DataSet dataSet, Point point, Map<String, String> tags) {
        OsmPrimitive node = buildNode(dataSet, point, tags, false);
        return node;
    }

    @Override
    public OsmPrimitive build(DataSet dataSet, LineString ls, Map<String, String> tags) {
        OsmPrimitive way = buildWay(dataSet, ls, tags);
        return way;
    }
    
    @Override
    public OsmPrimitive build(DataSet dataSet, MultiLineString mls, Map<String, String> tags) {
        // TODO implement this by creating an OdsPrimitiveGroup relation
        //        OsmPrimitive primitive = build((LineString)mls.getGeometryN(i), tags));
        return build(dataSet, (LineString)mls.getGeometryN(0), tags);
    }
    
    
    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildArea(com.vividsolutions.jts.geom.MultiPolygon)
     */
    @Override
    public OsmPrimitive buildArea(DataSet dataSet, MultiPolygon mpg, Map<String, String> tags) {
        OsmPrimitive primitive;
        if (mpg.getNumGeometries() > 1) {
            primitive = buildMultiPolygon(dataSet, mpg, tags);
            primitive.put("type", "multipolygon");
        } else {
            primitive = buildArea(dataSet, (Polygon) mpg.getGeometryN(0), tags);
        }
        return primitive;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildArea(com.vividsolutions.jts.geom.Polygon)
     */
    @Override
    public OsmPrimitive buildArea(DataSet dataSet, Polygon polygon, Map<String, String> tags) {
        OsmPrimitive primitive;
        if (polygon.getNumInteriorRing() > 0) {
            primitive = buildMultiPolygon(dataSet, polygon, tags);
            primitive.put("type", "multipolygon");
        }
        else {
            primitive = buildWay(dataSet, polygon, tags);
        }
        return primitive;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildMultiPolygon(com.vividsolutions.jts.geom.Polygon)
     */
    @Override
    public Relation buildMultiPolygon(DataSet dataSet, Polygon polygon, Map<String, String> tags) {
        MultiPolygon multiPolygon = polygon.getFactory().createMultiPolygon(
                new Polygon[] { polygon });
        return buildMultiPolygon(dataSet, multiPolygon, tags);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildMultiPolygon(com.vividsolutions.jts.geom.MultiPolygon)
     */
    @Override
    public Relation buildMultiPolygon(DataSet dataSet, MultiPolygon mpg, Map<String, String> tags) {
        Relation relation = new Relation();
        for (int i = 0; i < mpg.getNumGeometries(); i++) {
            Polygon polygon = (Polygon) mpg.getGeometryN(i);
            Way way = buildWay(dataSet, polygon.getExteriorRing(), null);
            relation.addMember(new RelationMember("outer", way));
            for (int j = 0; j < polygon.getNumInteriorRing(); j++) {
                way = buildWay(dataSet, polygon.getInteriorRingN(j), null);
                relation.addMember(new RelationMember("inner", way));
            }
        }
        relation.setKeys(tags);
        dataSet.addPrimitive(relation);
        return relation;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildWay(com.vividsolutions.jts.geom.Polygon)
     */
    @Override
    public Way buildWay(DataSet dataSet, Polygon polygon, Map<String, String> tags) {
        return buildWay(dataSet, polygon.getExteriorRing(), tags);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildWay(com.vividsolutions.jts.geom.LineString)
     */
    @Override
    public Way buildWay(DataSet dataSet, LineString line, Map<String, String> tags) {
        return buildWay(dataSet, line.getCoordinateSequence(), tags);
    }

    private Way buildWay(DataSet dataSet, CoordinateSequence points, Map<String, String> tags) {
        Way way = new Way();
        Node previousNode = null;
        for (int i = 0; i < points.size(); i++) {
            Node node = buildNode(dataSet, points.getCoordinate(i), null, true);
            // Remove duplicate nodes in ways
            if (node != previousNode) {
                way.addNode(node);
            }
            previousNode = node;
        }
        if (tags != null) way.setKeys(tags);
        dataSet.addPrimitive(way);
        return way;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildNode(com.vividsolutions.jts.geom.Coordinate, boolean)
     */
    @Override
    public Node buildNode(DataSet dataSet, Coordinate coordinate, Map<String, String> tags, boolean merge) {
        LatLon latlon = new LatLon(coordinate.y, coordinate.x).getRoundedToOsmPrecision();
        Node node = new Node(latlon);
        if (merge) {
            BBox bbox = new BBox(node);
            List<Node> existingNodes = dataSet.searchNodes(bbox);
            if (existingNodes.size() > 0) {
                node = existingNodes.get(0);
                return node;
            }
        }
        if (tags != null) node.setKeys(tags);
        dataSet.addPrimitive(node);
        return node;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildNode(com.vividsolutions.jts.geom.Point, boolean)
     */
    @Override
    public Node buildNode(DataSet dataSet, Point point, Map<String, String> tags, boolean merge) {
        if (point == null)
            return null;
        return buildNode(dataSet, point.getCoordinate(), tags, merge);
    }
}
