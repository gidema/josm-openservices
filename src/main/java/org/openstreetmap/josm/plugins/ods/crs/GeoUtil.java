package org.openstreetmap.josm.plugins.ods.crs;

import java.util.List;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.tools.Pair;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * This class provides methods to convert Josm geometries to JTS
 * geometries. Not coordinate tranformation is performed.
 * All source and target coordinates are in WGS84
 * 
 * @author gertjan
 *
 */
public class GeoUtil {
    private final static int OSM_SRID = 4326;
    protected final static PrecisionModel OSM_PRECISION_MODEL = new PrecisionModel(
            10000000);
    protected final static GeometryFactory OSM_GEOMETRY_FACTORY = new GeometryFactory(
            OSM_PRECISION_MODEL, OSM_SRID);
    private static GeoUtil instance = new GeoUtil();
    
    private GeoUtil() {
        // Hide public constructor
    }

    public static GeoUtil getInstance() {
        return instance;
    }
    
    public Coordinate toCoordinate(Node node) {
        return toCoordinate(node.getEastNorth());
    }
    
    public Coordinate toCoordinate(LatLon latLon) {
        return new Coordinate(latLon.getX(), latLon.getY());
    }
    
    public Coordinate toCoordinate(EastNorth en) {
        return new Coordinate(en.getX(), en.getY());
    }
    
    public Point toPoint(Node node) {
        return toPoint(toCoordinate(node));    
    }
    public Point toPoint(EastNorth en) {
        return toPoint(toCoordinate(en));    
    }
    
    public Point toPoint(Coordinate coord) {
        return OSM_GEOMETRY_FACTORY.createPoint(coord);
    }

    public Polygon toPolygon(Bounds bounds) {
        Coordinate[] coords = new Coordinate[5];
        coords[0] = new Coordinate(bounds.getMinLon(), bounds.getMinLat());
        coords[1] = new Coordinate(bounds.getMaxLon(), bounds.getMinLat());
        coords[2] = new Coordinate(bounds.getMaxLon(), bounds.getMaxLat());
        coords[3] = new Coordinate(bounds.getMinLon(), bounds.getMaxLat());
        coords[4] = new Coordinate(bounds.getMinLon(), bounds.getMinLat());
        LinearRing shell = OSM_GEOMETRY_FACTORY.createLinearRing(coords);
        return OSM_GEOMETRY_FACTORY.createPolygon(shell, null);
    }

    public LineSegment toSegment(Pair<Node, Node> nodePair) {
        return new LineSegment(toCoordinate(nodePair.a), toCoordinate(nodePair.b));
    }
    

    public Polygon toPolygon(Way way) throws InvalidPolygonException {
        LinearRing shell;
        try {
            shell = toLinearRing(way);
        } catch (UnclosedWayException e) {
            throw new InvalidPolygonException(way, 
                "The way that describes this polygon is not closed");
        }
        return OSM_GEOMETRY_FACTORY.createPolygon(shell, null);
    }

    public MultiPolygon toMultiPolygon(Way way) throws InvalidMultiPolygonException {
        Polygon polygon = null;
        try {
            polygon = toPolygon(way);
        } catch (InvalidPolygonException e) {
            throw new InvalidMultiPolygonException(way, e.getMessage());
        }
        return OSM_GEOMETRY_FACTORY.createMultiPolygon(new Polygon[] {polygon});
    }

    public MultiPolygon toMultiPolygon(Relation relation) throws InvalidMultiPolygonException {
        MultiPolygonBuilder builder = new MultiPolygonBuilder(relation);
        builder.build();
        return builder.getMultiPolygon();
    }
    
    public LineString toLineString(Way way) {
        Coordinate[] coords = new Coordinate[way.getNodes().size()];
        int i=0;
        for (Node node: way.getNodes()) {
            coords[i++] = toCoordinate(node);
        }
        return OSM_GEOMETRY_FACTORY.createLinearRing(coords);
    }

    public LinearRing toLinearRing(Way way) throws UnclosedWayException {
        if (!way.isClosed()) {
            throw new UnclosedWayException(way);
        }
        Coordinate[] coords = new Coordinate[way.getNodes().size() + 1];
        int i=0;
        for (Node node: way.getNodes()) {
            coords[i++] = toCoordinate(node);
        }
        coords[i] = coords[0];
        return OSM_GEOMETRY_FACTORY.createLinearRing(coords);
    }

    public Polygon toPolygon(LinearRing shell, List<LinearRing> innerRings) {
        return OSM_GEOMETRY_FACTORY.createPolygon(shell, innerRings.toArray(new LinearRing[0]));
    }

    public MultiPolygon toMultiPolygon(List<Polygon> polygons) {
        return OSM_GEOMETRY_FACTORY.createMultiPolygon(polygons.toArray(new Polygon[0]));
    }

    public Polygon toPolygon(Relation relation) throws InvalidPolygonException {
        MultiPolygon mpg;
        try {
            mpg = toMultiPolygon(relation);
        } catch (InvalidMultiPolygonException e) {
            throw new InvalidPolygonException(e.getPrimitive(), e.getMessage());
        }
        if (mpg.getNumGeometries() == 1) {
            return (Polygon) mpg.getGeometryN(0);
        }
        throw new InvalidPolygonException(relation,
            "Can't create a polygon Object from a relation that represents a multipolygon");
    }
}
