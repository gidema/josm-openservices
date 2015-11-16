package org.openstreetmap.josm.plugins.ods;

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
    private final LayerManager layerManager;

    public AbstractPrimitiveBuilder(LayerManager layerManager) {
        this.layerManager = layerManager;
    }

    @Override
    public LayerManager getLayerManager() {
        return layerManager;
    }
    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#build(com.vividsolutions.jts.geom.Geometry)
     */
    @Override
    public OsmPrimitive build(Geometry geometry, Map<String, String> tags) {
        tags.put(ODS.KEY.BASE, "true");
        switch (geometry.getGeometryType()) {
        case "Polygon":
            return build((Polygon)geometry, tags);
        case "MultiPolygon":
            return build((MultiPolygon)geometry, tags);
        case "Point":
            return build((Point)geometry, tags);
        case "LineString":
            return build((LineString)geometry, tags);
        case "MultiLineString":
            return build((MultiLineString)geometry, tags);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#build(com.vividsolutions.jts.geom.Polygon)
     */
    @Override
    public OsmPrimitive build(Polygon polygon, Map<String, String> tags) {
        return buildArea(polygon, tags);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#build(com.vividsolutions.jts.geom.MultiPolygon)
     */
    @Override
    public OsmPrimitive build(MultiPolygon mpg, Map<String, String> tags) {
        return buildArea(mpg, tags);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#build(com.vividsolutions.jts.geom.Point)
     */
    @Override
    public OsmPrimitive build(Point point, Map<String, String> tags) {
        OsmPrimitive node = buildNode(point, tags, false);
        return node;
    }

    @Override
    public OsmPrimitive build(LineString ls, Map<String, String> tags) {
        OsmPrimitive way = buildWay(ls, tags);
        return way;
    }
    
    @Override
    public OsmPrimitive build(MultiLineString mls, Map<String, String> tags) {
        // TODO implement this by creating an OdsPrimitiveGroup relation
        //        OsmPrimitive primitive = build((LineString)mls.getGeometryN(i), tags));
        return build((LineString)mls.getGeometryN(0), tags);
    }
    
    
    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildArea(com.vividsolutions.jts.geom.MultiPolygon)
     */
    @Override
    public OsmPrimitive buildArea(MultiPolygon mpg, Map<String, String> tags) {
        OsmPrimitive primitive;
        if (mpg.getNumGeometries() > 1) {
            primitive = buildMultiPolygon(mpg, tags);
            primitive.put("type", "multipolygon");
        } else {
            primitive = buildArea((Polygon) mpg.getGeometryN(0), tags);
        }
        return primitive;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildArea(com.vividsolutions.jts.geom.Polygon)
     */
    @Override
    public OsmPrimitive buildArea(Polygon polygon, Map<String, String> tags) {
        OsmPrimitive primitive;
        if (polygon.getNumInteriorRing() > 0) {
            primitive = buildMultiPolygon(polygon, tags);
            primitive.put("type", "multipolygon");
        }
        else {
            primitive = buildWay(polygon, tags);
        }
        return primitive;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildMultiPolygon(com.vividsolutions.jts.geom.Polygon)
     */
    @Override
    public Relation buildMultiPolygon(Polygon polygon, Map<String, String> tags) {
        MultiPolygon multiPolygon = polygon.getFactory().createMultiPolygon(
                new Polygon[] { polygon });
        return buildMultiPolygon(multiPolygon, tags);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildMultiPolygon(com.vividsolutions.jts.geom.MultiPolygon)
     */
    @Override
    public Relation buildMultiPolygon(MultiPolygon mpg, Map<String, String> tags) {
        Relation relation = new Relation();
        for (int i = 0; i < mpg.getNumGeometries(); i++) {
            Polygon polygon = (Polygon) mpg.getGeometryN(i);
            Way way = buildWay(polygon.getExteriorRing(), null);
            relation.addMember(new RelationMember("outer", way));
            for (int j = 0; j < polygon.getNumInteriorRing(); j++) {
                way = buildWay(polygon.getInteriorRingN(j), null);
                relation.addMember(new RelationMember("inner", way));
            }
        }
        relation.setKeys(tags);
        getDataSet().addPrimitive(relation);
        return relation;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildWay(com.vividsolutions.jts.geom.Polygon)
     */
    @Override
    public Way buildWay(Polygon polygon, Map<String, String> tags) {
        return buildWay(polygon.getExteriorRing(), tags);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildWay(com.vividsolutions.jts.geom.LineString)
     */
    @Override
    public Way buildWay(LineString line, Map<String, String> tags) {
        return buildWay(line.getCoordinateSequence(), tags);
    }

    private Way buildWay(CoordinateSequence points, Map<String, String> tags) {
        Way way = new Way();
        Node previousNode = null;
        for (int i = 0; i < points.size(); i++) {
            Node node = buildNode(points.getCoordinate(i), null, true);
            // Remove duplicate nodes in ways
            if (node != previousNode) {
                way.addNode(node);
            }
            previousNode = node;
        }
        if (tags != null) way.setKeys(tags);
        getDataSet().addPrimitive(way);
        return way;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildNode(com.vividsolutions.jts.geom.Coordinate, boolean)
     */
    @Override
    public Node buildNode(Coordinate coordinate, Map<String, String> tags, boolean merge) {
        LatLon latlon = new LatLon(coordinate.y, coordinate.x);
        Node node = new Node(latlon);
        if (merge) {
            BBox bbox = new BBox(node);
            List<Node> existingNodes = getDataSet().searchNodes(bbox);
            if (existingNodes.size() > 0) {
                node = existingNodes.get(0);
                return node;
            }
        }
        if (tags != null) node.setKeys(tags);
        getDataSet().addPrimitive(node);
        return node;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildNode(com.vividsolutions.jts.geom.Point, boolean)
     */
    @Override
    public Node buildNode(Point point, Map<String, String> tags, boolean merge) {
        if (point == null)
            return null;
        return buildNode(point.getCoordinate(), tags, merge);
    }

    private DataSet getDataSet() {
        return layerManager.getOsmDataLayer().data;
    }
    
    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#createPrimitives(T)
     */
    @Override
    public abstract void createPrimitive(T entity);
}
