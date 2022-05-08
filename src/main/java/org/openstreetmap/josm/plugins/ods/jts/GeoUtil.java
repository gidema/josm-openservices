package org.openstreetmap.josm.plugins.ods.jts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.visitor.paint.relations.Multipolygon.PolyData;
import org.openstreetmap.josm.plugins.ods.crs.UnclosedWayException;
import org.openstreetmap.josm.tools.Pair;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.WKTReader;

/**
 * This class provides methods to convert Josm geometries to JTS
 * geometries. No coordinate transformation is performed.
 * All source and target coordinates are in WGS84
 * 
 * TODO make method names more uniform
 * 
 * @author gertjan
 *
 */
public class GeoUtil {
    public final static int OSM_SRID = 4326;
    private final static PrecisionModel OSM_PRECISION_MODEL = new PrecisionModel();
//            10000000);
    public final static GeometryFactory OSM_GEOMETRY_FACTORY = new GeometryFactory(
            OSM_PRECISION_MODEL, OSM_SRID);
    public final static WKTReader OSM_WKT_READER = new WKTReader(OSM_GEOMETRY_FACTORY);
    private static GeoUtil instance = new GeoUtil();
    
    public GeoUtil() {
        // Hide public constructor
    }

    public static GeoUtil getInstance() {
        return instance;
    }
    
    public static Coordinate toCoordinate(Node node) {
        return toCoordinate(node.getCoor());
    }
    
    public static Coordinate toCoordinate(LatLon latLon) {
        return new Coordinate(latLon.getX(), latLon.getY());
    }
    
    public Point toPoint(Node node) {
        return toPoint(toCoordinate(node));    
    }
    
    public Point toPoint(LatLon latLon) {
        return toPoint(toCoordinate(latLon));    
    }
    
    @SuppressWarnings("static-method")
    public Point toPoint(Coordinate coord) {
        return OSM_GEOMETRY_FACTORY.createPoint(coord);
    }

    @SuppressWarnings("static-method")
    public LatLon toLatLon(Point point) {
        return new LatLon(point.getY(), point.getX());
    }
    
    public static LatLon toLatLon(Coordinate c) {
        return new LatLon(c.y, c.x);
    }
    
    @SuppressWarnings("static-method")
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

    @SuppressWarnings("static-method")
    public Polygon toPolygon(Envelope envelope) {
        Coordinate[] coords = new Coordinate[5];
        coords[0] = new Coordinate(envelope.getMinX(), envelope.getMinY());
        coords[1] = new Coordinate(envelope.getMaxX(), envelope.getMinY());
        coords[2] = new Coordinate(envelope.getMaxX(), envelope.getMaxY());
        coords[3] = new Coordinate(envelope.getMinX(), envelope.getMaxY());
        coords[4] = new Coordinate(envelope.getMinX(), envelope.getMinY());
        LinearRing shell = OSM_GEOMETRY_FACTORY.createLinearRing(coords);
        return OSM_GEOMETRY_FACTORY.createPolygon(shell, null);
    }

    @SuppressWarnings("static-method")
    public LineSegment toSegment(Pair<Node, Node> nodePair) {
        return new LineSegment(toCoordinate(nodePair.a), toCoordinate(nodePair.b));
    }
    

    public Polygon toPolygon(Way way) throws IllegalArgumentException {
        LinearRing shell;
        try {
            shell = toLinearRing(way);
        } catch (UnclosedWayException e) {
            throw new IllegalArgumentException(
                "The way that describes this polygon is not closed");
        }
        return OSM_GEOMETRY_FACTORY.createPolygon(shell, null);
    }

    public Polygon toPolygon(List<Coordinate> coords, LinearRing[] interiorRings) {
        LinearRing shell = toLinearRing(coords);
        return toPolygon(shell, interiorRings);
    }
    
    @SuppressWarnings("static-method")
    public Polygon toPolygon(LinearRing shell, LinearRing[] interiorRings) {
        return OSM_GEOMETRY_FACTORY.createPolygon(shell, interiorRings);
    }

    public MultiPolygon toMultiPolygon(Way way) throws IllegalArgumentException {
        Polygon polygon = toPolygon(way);
        return OSM_GEOMETRY_FACTORY.createMultiPolygon(new Polygon[] {polygon});
    }

    @SuppressWarnings("static-method")
    public MultiPolygon toMultiPolygon(Polygon polygon)  {
        return OSM_GEOMETRY_FACTORY.createMultiPolygon(new Polygon[] {polygon});
    }

    public Geometry toMultiPolygon(Relation relation) {
        MultiPolygonBuilder builder = new MultiPolygonBuilder(this, relation);
        builder.build();
        return builder.getGeometry();
    }
    
    @SuppressWarnings("static-method")
    public LineString toLineString(Way way) {
        Coordinate[] coords = new Coordinate[way.getNodes().size()];
        int i=0;
        for (Node node: way.getNodes()) {
            coords[i++] = toCoordinate(node);
        }
        return OSM_GEOMETRY_FACTORY.createLineString(coords);
    }

    @SuppressWarnings("static-method")
    public LineString toLineString(List<Coordinate> coords) {
        return OSM_GEOMETRY_FACTORY.createLineString(
            coords.toArray(new Coordinate[0]));
    }

    @SuppressWarnings("static-method")
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

    @SuppressWarnings("static-method")
    public LinearRing toLinearRing(List<Coordinate> coords) {
        return OSM_GEOMETRY_FACTORY.createLinearRing(
            coords.toArray(new Coordinate[0]));
    }

    @SuppressWarnings("static-method")
    public Polygon createPolygon(LinearRing shell, List<LinearRing> innerRings) {
        if (innerRings == null) {
            return OSM_GEOMETRY_FACTORY.createPolygon(shell, null);
        }
        return OSM_GEOMETRY_FACTORY.createPolygon(shell, innerRings.toArray(new LinearRing[0]));
    }

    @SuppressWarnings("static-method")
    public MultiPolygon createMultiPolygon(Collection<Polygon> polygons) {
        return OSM_GEOMETRY_FACTORY.createMultiPolygon(polygons.toArray(new Polygon[0]));
    }

    public Geometry buildGeometry(List<PolyData> polygons) {
        // TODO Find an other solution because PolyData doesn't
        // expose its shell and internal rings
        if (polygons.size() == 1) {
            return createPolygon(polygons.get(0));
        }
        List<Polygon> jtsPolygons = new ArrayList<>(polygons.size());
        for (PolyData polyData : polygons) {
            jtsPolygons.add(createPolygon(polyData));
        }
        return createMultiPolygon(jtsPolygons);
    }

    @SuppressWarnings("static-method")
    private Polygon createPolygon(@SuppressWarnings("unused") PolyData polyData) {
//        LinearRing shell = createLinearRing(polyData.)
        return null;
    }

    /**
     * Create a Josm Bounds object from a LinearRing.
     * The LinearRing coordinate are expected to be in WGS84
     *  
     * @param boundary
     * @return
     */
    @SuppressWarnings("static-method")
    public Bounds createBounds(LinearRing boundary) {
        Envelope e = boundary.getEnvelopeInternal();
        return new Bounds(e.getMinY(), e.getMinX(), e.getMaxY(), e.getMaxX());
    }

    public static LineSegment toLineSegment(Node n1, Node n2) {
        return toLineSegment(n1.getCoor(), n2.getCoor());
    }

    public static LineSegment toLineSegment(LatLon ll1, LatLon ll2) {
        return new LineSegment(ll1.lon(), ll2.lon(), ll1.lat(), ll2.lat());
    }

}
