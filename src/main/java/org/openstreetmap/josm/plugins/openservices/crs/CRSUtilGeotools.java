package org.openstreetmap.josm.plugins.openservices.crs;

import java.util.HashMap;
import java.util.Map;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Pair;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

public class CRSUtilGeotools extends AbstractCRSUtil {
    private final static int OSM_SRID = 4326;
    private final static CoordinateReferenceSystem OSM_CRS;
    private final static PrecisionModel OSM_PRECISION_MODEL = new PrecisionModel(
            10000000);
    public final static GeometryFactory OSM_GEOMETRY_FACTORY = new GeometryFactory(
            OSM_PRECISION_MODEL, OSM_SRID);
    private static Map<CoordinateReferenceSystem, MathTransform> toOsmTransforms = new HashMap<>();
    private static Map<CoordinateReferenceSystem, MathTransform> fromOsmTransforms = new HashMap<>();

    static {
        try {
            OSM_CRS = CRS.decode("EPSG:4326");
        } catch (NoSuchAuthorityCodeException e) {
            throw new RuntimeException(e);
        } catch (FactoryException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openstreetmap.josm.plugins.openservices.crs.AbstractCRSUtil#transform
     * (org.opengis.feature.simple.SimpleFeature)
     */
    @Override
    public Geometry transform(SimpleFeature feature) {
        MathTransform transform = getToOsmTransform(feature.getType()
                .getCoordinateReferenceSystem());
        try {
            return JTS.transform((Geometry) feature.getDefaultGeometry(),
                    transform);
        } catch (MismatchedDimensionException e) {
            throw new RuntimeException(e);
        } catch (TransformException e) {
            throw new RuntimeException(e);
        }
    }

    private MathTransform getToOsmTransform(CoordinateReferenceSystem crs) {
        MathTransform transform = toOsmTransforms.get(crs);
        if (transform == null) {
            try {
                transform = CRS.findMathTransform(crs, OSM_CRS);
                toOsmTransforms.put(crs, transform);
            } catch (FactoryException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return transform;
    }

    private MathTransform getFromOsmTransform(CoordinateReferenceSystem crs) {
        MathTransform transform = fromOsmTransforms.get(crs);
        if (transform == null) {
            try {
                transform = CRS.findMathTransform(OSM_CRS, crs);
                fromOsmTransforms.put(crs, transform);
            } catch (FactoryException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return transform;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openstreetmap.josm.plugins.openservices.crs.AbstractCRSUtil#toPolygon
     * (org.openstreetmap.josm.data.Bounds)
     */
    @Override
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openstreetmap.josm.plugins.openservices.crs.AbstractCRSUtil#toSegment
     * (org.openstreetmap.josm.tools.Pair)
     */
    @Override
    public LineSegment toSegment(Pair<Node, Node> nodePair) {
        return new LineSegment(toCoordinate(nodePair.a),
                toCoordinate(nodePair.b));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openstreetmap.josm.plugins.openservices.crs.AbstractCRSUtil#toCoordinate
     * (org.openstreetmap.josm.data.osm.Node)
     */
    @Override
    public Coordinate toCoordinate(Node node) {
        return toCoordinate(node.getEastNorth());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openstreetmap.josm.plugins.openservices.crs.AbstractCRSUtil#toCoordinate
     * (org.openstreetmap.josm.data.coor.LatLon)
     */
    @Override
    public Coordinate toCoordinate(LatLon latLon) {
        return new Coordinate(latLon.getX(), latLon.getY());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openstreetmap.josm.plugins.openservices.crs.AbstractCRSUtil#toCoordinate
     * (org.openstreetmap.josm.data.coor.EastNorth)
     */
    @Override
    public Coordinate toCoordinate(EastNorth en) {
        return new Coordinate(en.getX(), en.getY());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openstreetmap.josm.plugins.openservices.crs.AbstractCRSUtil#
     * createBoundingBox(org.opengis.referencing.crs.CoordinateReferenceSystem,
     * org.openstreetmap.josm.data.Bounds)
     */
    @Override
    public ReferencedEnvelope createBoundingBox(CoordinateReferenceSystem crs,
            Bounds bounds) throws CRSException {
        Envelope osmEnvelope = new Envelope(bounds.getMinLon(),
                bounds.getMaxLon(), bounds.getMinLat(), bounds.getMaxLat());
        MathTransform transform;
        try {
            transform = getFromOsmTransform(crs);
            Envelope targetEnvelope = JTS.transform(osmEnvelope, transform);
            return new ReferencedEnvelope(targetEnvelope, OSM_CRS);
        } catch (MismatchedDimensionException e) {
            throw new CRSException(I18n.tr(e.getMessage()));
        } catch (TransformException e) {
            throw new CRSException(I18n.tr(
                    "Couldn't create a bounding box for "
                            + "the coordinate reference system {0}.",
                    crs.getName()));
        }
    }
}
