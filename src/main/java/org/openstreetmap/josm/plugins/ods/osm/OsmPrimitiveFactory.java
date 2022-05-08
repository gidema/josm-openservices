package org.openstreetmap.josm.plugins.ods.osm;

import java.util.Map;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Build OsmPrimitives from a JTS geometry and a set of tags and add them to a josm dataset.
 * The JTS geometries must be in the josm crs (epsg:4326)
 * The methods take care of removal of duplicate nodes in ways, and merging of nodes that refer to the same point.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface OsmPrimitiveFactory {
    public OsmPrimitive create(DataSet dataSet, Geometry geometry, Map<String, String> tags);

    public OsmPrimitive create(DataSet dataSet, Polygon polygon, Map<String, String> tags);

    public OsmPrimitive build(DataSet dataSet, MultiPolygon mpg, Map<String, String> tags);

    public OsmPrimitive build(DataSet dataSet, Point point, Map<String, String> tags);

    public OsmPrimitive build(DataSet dataSet, LineString ls, Map<String, String> tags);

    public OsmPrimitive build(DataSet dataSet, MultiLineString mls, Map<String, String> tags);

    /**
     * Create a josm Object from a MultiPolygon object The resulting Object depends
     * on whether the input Multipolygon consists of multiple polygons. If so, the result will be a
     * Relation of type Multipolyon. Otherwise the single polygon will be built.
     */
    public OsmPrimitive buildArea(DataSet dataSet, MultiPolygon mpg, Map<String, String> tags);

    /**
     * Create a josm Object from a Polygon object The resulting Object depends
     * on whether the input polygon has inner rings. If so, the result will be a
     * Relation of type Multipolyon. Otherwise the result will be a Way
     */
    public OsmPrimitive buildArea(DataSet dataSet, Polygon polygon, Map<String, String> tags);

    /**
     * Create a josm MultiPolygon relation from a Polygon object.
     * 
     * @param polygon
     * @return the relation
     */
    public Relation buildMultiPolygon(DataSet dataSet, Polygon polygon, Map<String, String> tags);

    /**
     * Create a josm MultiPolygon relation from a MultiPolygon object.
     * 
     * @param mpg
     * @return the relation
     */
    public Relation buildMultiPolygon(DataSet dataSet, MultiPolygon mpg, Map<String, String> tags);

    /**
     * Create a josm Way from the exterior ring of a Polygon object
     * 
     * @param polygon
     * @return the way
     */
    public Way buildWay(DataSet dataSet, Polygon polygon, Map<String, String> tags);

    /**
     * Create a josm way from a LineString object
     * 
     * @param line
     * @return
     */
    public Way buildWay(DataSet dataSet, LineString line, Map<String, String> tags);

    /**
     * Create a josm Node from a Coordinate object. Optionally merge with
     * existing nodes.
     * 
     * @param coordinate
     * @param merge
     *            if true, merge this node with an existing node
     * @return the node
     */
    public Node buildNode(DataSet dataSet, Coordinate coordinate, Map<String, String> tags, boolean merge);

    /**
     * Create a josm Node from a Point object. Optionally merge with existing
     * nodes.
     * 
     * @param point
     * @param merge
     * @return
     */
    public Node buildNode(DataSet dataSet, Point point, Map<String, String> tags, boolean merge);
}