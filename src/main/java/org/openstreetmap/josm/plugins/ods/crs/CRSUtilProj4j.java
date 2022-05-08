package org.openstreetmap.josm.plugins.ods.crs;

import java.util.HashMap;
import java.util.Map;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.data.Bounds;

/**
 * A Proj4j based implementation of CRSUtils. The opengis Mathtransform has
 * issues with EPSG:28992
 * 
 * @author gertjan
 * 
 */
public class CRSUtilProj4j extends CRSUtil {
    private final static JTSCoordinateTransformFactory ctFactory = new Proj4jCRSTransformFactory();
//    private final static Double OSM_SCALE = 10000000;
    private final static Double OSM_SCALE = null;
//    private final static PrecisionModel OSM_PRECISION_MODEL =
//        (OSM_SCALE == null ? new PrecisionModel() : new PrecisionModel(OSM_SCALE));
//    public final static GeometryFactory OSM_GEOMETRY_FACTORY = new GeometryFactory(
//            OSM_PRECISION_MODEL, OSM_SRID.intValue());
    private static Map<Long[], JTSCoordinateTransform> transforms = new HashMap<>();
//    private static Map<Integer, JTSCoordinateTransform> fromOsmTransforms = new HashMap<>();

    @Override
    public Geometry fromOsm(Geometry geometry, Long srid) {
        JTSCoordinateTransform transform = getTransform(CRSUtil.OSM_SRID, srid);
        return transform.transform(geometry);
    }

    @Override
    public Geometry toOsm(Geometry geometry, Long srid) {
        JTSCoordinateTransform transform = getTransform(srid, CRSUtil.OSM_SRID);
        return transform.transform(geometry);
    }

    @Override
    public Geometry transform(Geometry geometry, Long targetSrid) {
        return transform(geometry, Long.valueOf(geometry.getSRID()), targetSrid);
    }

    static Geometry transform(Geometry geometry, Long sourceSrid, Long targetSrid) {
        JTSCoordinateTransform transform = getTransform(sourceSrid, targetSrid);
        return transform.transform(geometry);
    }
    
    private static synchronized JTSCoordinateTransform getTransform(
          Long sourceSrid, Long targetSrid) {
        JTSCoordinateTransform transform = transforms.get(new Long[] {sourceSrid, targetSrid});
        if (transform == null) {
            transform = ctFactory.createJTSCoordinateTransform(sourceSrid, targetSrid, OSM_SCALE);
            transforms.put(new Long[] {sourceSrid, targetSrid}, transform);
        }
        return transform;
    }

    public static Envelope toEnvelope(Bounds bounds) {
        return new Envelope(bounds.getMinLon(), bounds.getMaxLon(),
                bounds.getMinLat(), bounds.getMaxLat());
    }
}
