package org.openstreetmap.josm.plugins.openservices.crs;

import java.util.HashMap;
import java.util.Map;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.openstreetmap.josm.data.Bounds;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

public class CRSUtil {
    private final static CoordinateReferenceSystem OSM_CRS;
    private final static PrecisionModel OSM_PRECISION_MODEL =
            new PrecisionModel(10000000);
    public final static GeometryFactory OSM_GEOMETRY_FACTORY =
            new GeometryFactory(OSM_PRECISION_MODEL, 4326);
    private static Map<CoordinateReferenceSystem, MathTransform> transforms = new HashMap<>();

    static {
        try {
            OSM_CRS = CRS.decode("EPSG:4326");
        } catch (NoSuchAuthorityCodeException e) {
            throw new RuntimeException(e);
        } catch (FactoryException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getSrs(CoordinateReferenceSystem crs) {
        return getIdentifier(crs).toString();
    }

    public static Integer getSrid(CoordinateReferenceSystem crs) {
        return Integer.valueOf(getIdentifier(crs).getCode());
    }

    private static ReferenceIdentifier getIdentifier(
            CoordinateReferenceSystem crs) {
        return crs.getIdentifiers().iterator().next();
    }

    public static Geometry transform(SimpleFeature feature) {
        MathTransform transform = getMathTransform(feature.getType()
                .getCoordinateReferenceSystem());
        try {
            return JTS.transform((Geometry)feature.getDefaultGeometry(), transform);
        } catch (MismatchedDimensionException e) {
            throw new RuntimeException(e);
        } catch (TransformException e) {
            throw new RuntimeException(e);
        }
    }

    private static MathTransform getMathTransform(CoordinateReferenceSystem crs) {
        MathTransform transform = transforms.get(crs);
        if (transform == null) {
            try {
                transform = CRS.findMathTransform(crs, OSM_CRS);
            } catch (FactoryException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return transform;
    }
    
    public static Polygon toPolygon(Bounds bounds) {
        Coordinate[] coords = new Coordinate[5];
        coords[0] = new Coordinate(bounds.getMinLon(), bounds.getMinLat());
        coords[1] = new Coordinate(bounds.getMaxLon(), bounds.getMinLat());
        coords[2] = new Coordinate(bounds.getMaxLon(), bounds.getMaxLat());
        coords[3] = new Coordinate(bounds.getMinLon(), bounds.getMaxLat());
        coords[4] = new Coordinate(bounds.getMinLon(), bounds.getMinLat());
        LinearRing shell = OSM_GEOMETRY_FACTORY.createLinearRing(coords);
        return OSM_GEOMETRY_FACTORY.createPolygon(shell, null);
    }
}
