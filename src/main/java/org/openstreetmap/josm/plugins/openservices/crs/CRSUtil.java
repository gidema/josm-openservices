package org.openstreetmap.josm.plugins.openservices.crs;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.tools.Pair;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Polygon;

public class CRSUtil {
    private static AbstractCRSUtil util = new CRSUtilProj4j();
    public static String getSrs(CoordinateReferenceSystem crs) {
        return util.getSrs(crs);
    }

    public static Integer getSrid(CoordinateReferenceSystem crs) {
        return util.getSrid(crs);
    }

    public static Geometry transform(SimpleFeature feature) throws CRSException {
        return util.transform(feature);
    }

    
    public static Polygon toPolygon(Bounds bounds) {
        return util.toPolygon(bounds);
    }
    
    public static LineSegment toSegment(Pair<Node, Node> nodePair) {
        return util.toSegment(nodePair);
    }
    
    public static Coordinate toCoordinate(Node node) {
        return util.toCoordinate(node);
    }
    
    public static Coordinate toCoordinate(LatLon latLon) {
        return util.toCoordinate(latLon);
    }
    
    public static Coordinate toCoordinate(EastNorth en) {
        return util.toCoordinate(en);
    }
    
    /**
     * Create a ReferencedEnvelope from a Josm bounds object, using the supplied CoordinateReferenceSystem
     * 
     * @param crs
     * @param bounds
     * @return
     * @throws TransformException 
     */
    public static ReferencedEnvelope createBoundingBox(CoordinateReferenceSystem crs, Bounds bounds) throws CRSException {
        return util.createBoundingBox(crs, bounds);
    }

    public static CoordinateReferenceSystem getCrs(Long srid) throws CRSException {
        String srs = "EPSG:" + srid.toString();
        return getCrs(srs);
    }

    public static CoordinateReferenceSystem getCrs(String srs) throws CRSException {
        return util.getCrs(srs);
    }
}
