package org.openstreetmap.josm.plugins.openservices.crs;

import java.util.HashMap;
import java.util.Map;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Pair;
import org.osgeo.proj4j.util.CRSCache;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
//import org.osgeo.proj4j.CoordinateReferenceSystem;

/**
 * A Proj4j based implementation of CRSUtils.
 * The opengis Mathtransform has issues with EPSG:28992
 * @author gertjan
 *
 */
public class CRSUtilProj4j extends CRSUtil {
    private final static CRSCache crsCache = new CRSCache();
    private final static Long OSM_SRID = 4326L;
    private final static org.osgeo.proj4j.CoordinateReferenceSystem OSM_CRS;
    private final static JTSCoordinateTransformFactory ctFactory = new Proj4jCRSTransformFactory();
    private final static PrecisionModel OSM_PRECISION_MODEL =
            new PrecisionModel(10000000);
    public final static GeometryFactory OSM_GEOMETRY_FACTORY =
            new GeometryFactory(OSM_PRECISION_MODEL, OSM_SRID.intValue());
    private static Map<String, CoordinateReferenceSystem> coordinateReferenceSystems = new HashMap<String, CoordinateReferenceSystem>();
    private static Map<CoordinateReferenceSystem, JTSCoordinateTransform> toOsmTransforms = new HashMap<CoordinateReferenceSystem, JTSCoordinateTransform>();
    private static Map<CoordinateReferenceSystem, JTSCoordinateTransform> fromOsmTransforms = new HashMap<CoordinateReferenceSystem, JTSCoordinateTransform>();

    static {
        OSM_CRS = crsCache.createFromName("EPSG:4326");
    }

//    public static String getSrs(CoordinateReferenceSystem crs) {
//        return getIdentifier(crs).toString();
//    }

//    public static Integer getSrid(CoordinateReferenceSystem crs) {
//        return Integer.valueOf(getIdentifier(crs).getCode());
//    }

//    private static ReferenceIdentifier getIdentifier(
//            CoordinateReferenceSystem crs) {
//        return crs.getIdentifiers().iterator().next();
//    }

    public Geometry transform(SimpleFeature feature) throws CRSException {
        JTSCoordinateTransform transform = getToOsmTransform(feature.getType()
                .getCoordinateReferenceSystem());
        try {
            return transform.transform((Geometry)feature.getDefaultGeometry());
        } catch (MismatchedDimensionException e) {
            throw new RuntimeException(e);
        }
    }

    private static JTSCoordinateTransform getToOsmTransform(
        CoordinateReferenceSystem crs) {
        JTSCoordinateTransform transform = toOsmTransforms.get(crs);
        if (transform == null) {
            Long sourceSRID = getSRID(crs);
            transform = ctFactory.createJTSCoordinateTransform(sourceSRID, OSM_SRID);
            toOsmTransforms.put(crs,  transform);
        }
        return transform;
    }
    
    private static Long getSRID(CoordinateReferenceSystem crs) {
        String srs = CRS.toSRS(crs);
        return Long.parseLong(srs.substring(5));
    }
    
    private static JTSCoordinateTransform getFromOsmTransform(CoordinateReferenceSystem crs) {
        JTSCoordinateTransform transform = fromOsmTransforms.get(crs);
        if (transform == null) {
            Long sourceSRID = getSRID(crs);
            transform = ctFactory.createJTSCoordinateTransform(OSM_SRID, sourceSRID);
            toOsmTransforms.put(crs,  transform);
        }
        return transform;
    }
    
    public static Envelope toEnvelope(Bounds bounds) {
        return new Envelope(bounds.getMinLon(), bounds.getMaxLon(),
            bounds.getMinLat(), bounds.getMaxLat());
    }
    
    /**
     * Create a ReferencedEnvelope from a Josm bounds object, using the supplied CoordinateReferenceSystem
     * 
     * @param crs
     * @param bounds
     * @return
     * @throws TransformException 
     */
    public ReferencedEnvelope createBoundingBox(CoordinateReferenceSystem crs, Bounds bounds) throws CRSException {
      Envelope envelope = toEnvelope(bounds);
      JTSCoordinateTransform transform;
    try {
        transform = getFromOsmTransform(crs);
        Envelope targetEnvelope = transform.transform(envelope);
        return new ReferencedEnvelope(targetEnvelope, getCrs(4326L));
    } catch (MismatchedDimensionException e) {
        throw new CRSException(I18n.tr(e.getMessage()));
//    } catch (TransformException e) {
//        throw new CRSException(I18n.tr("Couldn't create a bounding box for " + 
//            "the coordinate reference system {0}.", crs.getName()));
    }
    }
}
