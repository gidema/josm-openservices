package org.openstreetmap.josm.plugins.ods;

import java.util.List;

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

    public List<OsmPrimitive> build(Geometry geometry);

    public List<OsmPrimitive> build(Polygon polygon);

    public List<OsmPrimitive> build(MultiPolygon mpg);

    public List<OsmPrimitive> build(Point point);

    public List<OsmPrimitive> build(LineString ls);

    public List<OsmPrimitive> build(MultiLineString mls);

    /**
     * Create a josm Object from a MultiPolygon object The resulting Object depends
     * on whether the input Multipolygon consists of multiple polygons. If so, the result will be a
     * Relation of type Multipolyon. Otherwise the single polygon will be built.
     */
    public OsmPrimitive buildArea(MultiPolygon mpg);

    /**
     * Create a josm Object from a Polygon object The resulting Object depends
     * on whether the input polygon has inner rings. If so, the result will be a
     * Relation of type Multipolyon. Otherwise the result will be a Way
     */
    public OsmPrimitive buildArea(Polygon polygon);

    /**
     * Create a josm MultiPolygon relation from a Polygon object.
     * 
     * @param polygon
     * @return the relation
     */
    public Relation buildMultiPolygon(Polygon polygon);

    /**
     * Create a josm MultiPolygon relation from a MultiPolygon object.
     * 
     * @param mpg
     * @return the relation
     */
    public Relation buildMultiPolygon(MultiPolygon mpg);

    /**
     * Create a josm Way from the exterior ring of a Polygon object
     * 
     * @param polygon
     * @return the way
     */
    public Way buildWay(Polygon polygon);

    /**
     * Create a josm way from a LineString object
     * 
     * @param line
     * @return
     */
    public Way buildWay(LineString line);

    /**
     * Create a josm Node from a Coordinate object. Optionally merge with
     * existing nodes.
     * 
     * @param coordinate
     * @param merge
     *            if true, merge this node with an existing node
     * @return the node
     */
    public Node buildNode(Coordinate coordinate, boolean merge);

    /**
     * Create a josm Node from a Point object. Optionally merge with existing
     * nodes.
     * 
     * @param point
     * @param merge
     * @return
     */
    public Node buildNode(Point point, boolean merge);

    public void createPrimitives(T entity);

}