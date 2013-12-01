package org.openstreetmap.josm.plugins.ods.crs;

import java.util.HashMap;
import java.util.Map;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.tools.I18n;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

public abstract class CRSUtil {
    private final static CRSUtil instance = new CRSUtilProj4j();
    private final static int OSM_SRID = 4326;
    protected final static CoordinateReferenceSystem OSM_CRS;
    protected final static PrecisionModel OSM_PRECISION_MODEL = new PrecisionModel(
            10000000);
    protected final static GeometryFactory OSM_GEOMETRY_FACTORY = new GeometryFactory(
            OSM_PRECISION_MODEL, OSM_SRID);
    private static Map<String, CoordinateReferenceSystem> coordinateReferenceSystems = new HashMap<>();

    static {
        try {
            OSM_CRS = CRS.decode("EPSG:4326");
        } catch (NoSuchAuthorityCodeException e) {
            throw new RuntimeException(e);
        } catch (FactoryException e) {
            throw new RuntimeException(e);
        }
    }
    
    public synchronized static CRSUtil getInstance() {
        return instance;
    }
    
    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.crs.CRSUtil#getSrs(org.opengis.referencing.crs.CoordinateReferenceSystem)
     */
    public String getSrs(CoordinateReferenceSystem crs) {
        return getIdentifier(crs).toString();
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.crs.CRSUtil#getSrid(org.opengis.referencing.crs.CoordinateReferenceSystem)
     */
    public Integer getSrid(CoordinateReferenceSystem crs) {
        return Integer.valueOf(getIdentifier(crs).getCode());
    }

    private ReferenceIdentifier getIdentifier(
            CoordinateReferenceSystem crs) {
        return crs.getIdentifiers().iterator().next();
    }

    public abstract Geometry transform(SimpleFeature feature) throws CRSException;

    /**
     * Create a ReferencedEnvelope from a Josm bounds object, using the supplied CoordinateReferenceSystem
     * 
     * @param crs
     * @param bounds
     * @return
     * @throws TransformException 
     */
    public abstract ReferencedEnvelope createBoundingBox(CoordinateReferenceSystem crs,
            Bounds bounds) throws CRSException;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openstreetmap.josm.plugins.ods.crs.CRSUtil#getCrs
     * (java.lang.Long)
     */
    public CoordinateReferenceSystem getCrs(Long srid) throws CRSException {
        String srs = "EPSG:" + srid.toString();
        return getCrs(srs);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openstreetmap.josm.plugins.ods.crs.CRSUtil#getCrs
     * (java.lang.String)
     */
    public CoordinateReferenceSystem getCrs(String srs) throws CRSException {
        CoordinateReferenceSystem crs = coordinateReferenceSystems.get(srs);
        try {
            if (crs == null) {
                crs = CRS.decode(srs);
                coordinateReferenceSystems.put(srs, crs);
            }
        } catch (NoSuchAuthorityCodeException e) {
            throw new CRSException(I18n.tr(
                    "The supplied coordinate reference system "
                            + "{0} is unknown", srs));
        } catch (FactoryException e) {
            throw new CRSException(I18n.tr("Unable to create a "
                    + "coordinate reference system for {0}", srs));
        }
        return crs;
    }

//    public Coordinate toCoordinate(Node node) {
//        return toCoordinate(node.getEastNorth());
//    }
//    
//    public Coordinate toCoordinate(LatLon latLon) {
//        return new Coordinate(latLon.getX(), latLon.getY());
//    }
//    
//    public Coordinate toCoordinate(EastNorth en) {
//        return new Coordinate(en.getX(), en.getY());
//    }
//    
//    public Point toPoint(Node node) {
//        return toPoint(toCoordinate(node));    
//    }
//    public Point toPoint(EastNorth en) {
//        return toPoint(toCoordinate(en));    
//    }
//    
//    public Point toPoint(Coordinate coord) {
//        return OSM_GEOMETRY_FACTORY.createPoint(coord);
//    }
//
//    public Polygon toPolygon(Way way) {
//        LinearRing shell = toLinearRing(way);
//        return OSM_GEOMETRY_FACTORY.createPolygon(shell, null);
//    }
//
//    public Polygon toPolygon(Relation relation) {
//        Way outerWay;
//        List<Way> innerWays = new LinkedList<Way>();
//        for (RelationMember member : relation.getMembers())
//            if ("outer".equals(member.getRole())) {
////                member.getMember()
//            }
//        return null;
//    }
//    
//    public LineString toLineString(Way way) {
//        Coordinate[] coords = new Coordinate[way.getNodes().size()];
//        int i=0;
//        for (Node node: way.getNodes()) {
//            coords[i++] = CRSUtils.toCoordinate(node);
//        }
//        return OSM_GEOMETRY_FACTORY.createLinearRing(coords);
//    }
//
//    public LinearRing toLinearRing(Way way) {
//        Coordinate[] coords = new Coordinate[way.getNodes().size() + 1];
//        int i=0;
//        for (Node node: way.getNodes()) {
//            coords[i++] = CRSUtils.toCoordinate(node);
//        }
//        coords[i] = coords[0];
//        return OSM_GEOMETRY_FACTORY.createLinearRing(coords);
//    }

}