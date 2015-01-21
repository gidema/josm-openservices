package org.openstreetmap.josm.plugins.ods;

import java.util.Map;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public interface PrimitiveBuilder<T extends Entity> {

    public OsmPrimitive build(Geometry geometry, Map<String, String> tags);

    public OsmPrimitive build(Polygon polygon, Map<String, String> tags);

    public OsmPrimitive build(MultiPolygon mpg, Map<String, String> tags);

    public OsmPrimitive build(Point point, Map<String, String> tags);

    public OsmPrimitive build(LineString ls, Map<String, String> tags);

    public OsmPrimitive build(MultiLineString mls, Map<String, String> tags);

    /**
     * Create a josm Object from a MultiPolygon object The resulting Object depends
     * on whether the input Multipolygon consists of multiple polygons. If so, the result will be a
     * Relation of type Multipolyon. Otherwise the single polygon will be built.
     */
    public OsmPrimitive buildArea(MultiPolygon mpg, Map<String, String> tags);

    /**
     * Create a josm Object from a Polygon object The resulting Object depends
     * on whether the input polygon has inner rings. If so, the result will be a
     * Relation of type Multipolyon. Otherwise the result will be a Way
     */
    public OsmPrimitive buildArea(Polygon polygon, Map<String, String> tags);

    /**
     * Create a josm MultiPolygon relation from a Polygon object.
     * 
     * @param polygon
     * @return the relation
     */
    public Relation buildMultiPolygon(Polygon polygon, Map<String, String> tags);

    /**
     * Create a josm MultiPolygon relation from a MultiPolygon object.
     * 
     * @param mpg
     * @return the relation
     */
    public Relation buildMultiPolygon(MultiPolygon mpg, Map<String, String> tags);

    /**
     * Create a josm Way from the exterior ring of a Polygon object
     * 
     * @param polygon
     * @return the way
     */
    public Way buildWay(Polygon polygon, Map<String, String> tags);

    /**
     * Create a josm way from a LineString object
     * 
     * @param line
     * @return
     */
    public Way buildWay(LineString line, Map<String, String> tags);

    /**
     * Create a josm Node from a Coordinate object. Optionally merge with
     * existing nodes.
     * 
     * @param coordinate
     * @param merge
     *            if true, merge this node with an existing node
     * @return the node
     */
    public Node buildNode(Coordinate coordinate, Map<String, String> tags, boolean merge);

    /**
     * Create a josm Node from a Point object. Optionally merge with existing
     * nodes.
     * 
     * @param point
     * @param merge
     * @return
     */
    public Node buildNode(Point point, Map<String, String> tags, boolean merge);

    public void createPrimitive(T entity);
}