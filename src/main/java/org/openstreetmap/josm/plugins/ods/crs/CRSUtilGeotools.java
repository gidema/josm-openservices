package org.openstreetmap.josm.plugins.ods.crs;

import java.util.HashMap;
import java.util.Map;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.tools.I18n;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class CRSUtilGeotools extends CRSUtil {
    private static Map<CoordinateReferenceSystem, MathTransform> toOsmTransforms = new HashMap<>();
    private static Map<CoordinateReferenceSystem, MathTransform> fromOsmTransforms = new HashMap<>();

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openstreetmap.josm.plugins.ods.crs.CRSUtil#transform
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

    @Override
    public Geometry toOsm(Geometry geometry, CoordinateReferenceSystem crs)
            throws CRSException {
        MathTransform transform = getToOsmTransform(crs);
        try {
            return JTS.transform(geometry, transform);
        } catch (MismatchedDimensionException e) {
            throw new RuntimeException(e);
        } catch (TransformException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Geometry fromOsm(Geometry geometry, CoordinateReferenceSystem crs)
            throws CRSException {
        MathTransform transform = getFromOsmTransform(crs);
        try {
            return JTS.transform(geometry, transform);
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
     * @see org.openstreetmap.josm.plugins.ods.crs.CRSUtil#
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
                    crs.getName()), e);
        }
    }
}
