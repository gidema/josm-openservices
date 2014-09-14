package org.openstreetmap.josm.plugins.ods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * The AbstractPrimitiveBuilder provides methods to create josm primitives from JTS
 * geometries and add them to a josm dataset. The JTS geometries must be in the
 * josm crs (epsg:4326) The methods take care of removal of duplicate nodes in
 * ways, and merging of nodes that refer to the same point.
 * TODO split Interface and implementation
 * 
 * @author Gertjan Idema
 * 
 */
public abstract class AbstractPrimitiveBuilder<T extends Entity> implements PrimitiveBuilder<T> {
    private final DataSet dataSet;

    /**
     * Create a JosmSourceManager with the specified source crs
     * 
     * @param sourceCrs
     */
    public AbstractPrimitiveBuilder(DataSet targetDataSet) {
        this.dataSet = targetDataSet;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#build(com.vividsolutions.jts.geom.Geometry)
     */
    @Override
    public List<OsmPrimitive> build(Geometry geometry) {
        switch (geometry.getGeometryType()) {
        case "Polygon":
            return build((Polygon)geometry);
        case "MultiPolygon":
            return build((MultiPolygon)geometry);
        case "Point":
            return build((Point)geometry);
        case "LineString":
            return build((LineString)geometry);
        case "MultiLineString":
            return build((MultiLineString)geometry);
        }
        return Arrays.asList(new OsmPrimitive[0]);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#build(com.vividsolutions.jts.geom.Polygon)
     */
    @Override
    public List<OsmPrimitive> build(Polygon polygon) {
        return Collections.singletonList(buildArea(polygon));
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#build(com.vividsolutions.jts.geom.MultiPolygon)
     */
    @Override
    public List<OsmPrimitive> build(MultiPolygon mpg) {
        return Collections.singletonList(buildArea(mpg));
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#build(com.vividsolutions.jts.geom.Point)
     */
    @Override
    public List<OsmPrimitive> build(Point point) {
        OsmPrimitive node = buildNode(point, false);
        return Collections.singletonList(node);
    }

    @Override
    public List<OsmPrimitive> build(LineString ls) {
        OsmPrimitive way = buildWay(ls);
        return Collections.singletonList(way);
    }
    
    @Override
    public List<OsmPrimitive> build(MultiLineString mls) {
        int count = mls.getNumGeometries();
        List<OsmPrimitive> primitives = new ArrayList<>(count);
        for (int i = 0; i<count; i++) {
            primitives.addAll(build((LineString)mls.getGeometryN(i)));
        }
        return primitives;
    }
    
    
    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildArea(com.vividsolutions.jts.geom.MultiPolygon)
     */
    @Override
    public OsmPrimitive buildArea(MultiPolygon mpg) {
        OsmPrimitive primitive;
        if (mpg.getNumGeometries() > 1) {
            primitive = buildMultiPolygon(mpg);
            primitive.put("type", "multipolygon");
        } else {
            primitive = buildArea((Polygon) mpg.getGeometryN(0));
        }
        return primitive;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildArea(com.vividsolutions.jts.geom.Polygon)
     */
    @Override
    public OsmPrimitive buildArea(Polygon polygon) {
        OsmPrimitive primitive;
        if (polygon.getNumInteriorRing() > 0) {
            primitive = buildMultiPolygon(polygon);
            primitive.put("type", "multipolygon");
        }
        else {
            primitive = buildWay(polygon);
        }
        return primitive;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildMultiPolygon(com.vividsolutions.jts.geom.Polygon)
     */
    @Override
    public Relation buildMultiPolygon(Polygon polygon) {
        MultiPolygon multiPolygon = polygon.getFactory().createMultiPolygon(
                new Polygon[] { polygon });
        return buildMultiPolygon(multiPolygon);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildMultiPolygon(com.vividsolutions.jts.geom.MultiPolygon)
     */
    @Override
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

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildWay(com.vividsolutions.jts.geom.Polygon)
     */
    @Override
    public Way buildWay(Polygon polygon) {
        return buildWay(polygon.getExteriorRing());
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildWay(com.vividsolutions.jts.geom.LineString)
     */
    @Override
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

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildNode(com.vividsolutions.jts.geom.Coordinate, boolean)
     */
    @Override
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

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildNode(com.vividsolutions.jts.geom.Point, boolean)
     */
    @Override
    public Node buildNode(Point point, boolean merge) {
        if (point == null)
            return null;
        return buildNode(point.getCoordinate(), merge);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#createPrimitives(T)
     */
    @Override
    public abstract void createPrimitives(T entity);
}
