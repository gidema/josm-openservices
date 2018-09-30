package org.openstreetmap.josm.plugins.ods.entities.opendata;

import java.math.BigDecimal;

import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.Normalisation;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.CoordinateArrays;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class FeatureUtil {
    public static Double getDouble(SimpleFeature feature, String name ) {
        Property property = feature.getProperty(name);
        if (property == null) return null;
        return (Double) property.getValue();
    }

    public static BigDecimal getBigDecimal(SimpleFeature feature, String name ) {
        Property property = feature.getProperty(name);
        if (property == null) return null;
        return (BigDecimal) property.getValue();
    }

    public static String getString(SimpleFeature feature, String name ) {
        Property property = feature.getProperty(name);
        if (property == null) return null;
        return (String) property.getValue();
    }

    public static Integer getInteger(SimpleFeature feature, String name ) {
        Property property = feature.getProperty(name);
        if (property == null) return null;
        Number number = (Number) property.getValue();
        if (number == null) return null;
        return number.intValue();
    }

    public static Long getLong(SimpleFeature feature, String name ) {
        Property property = feature.getProperty(name);
        if (property == null) return null;
        Number number = (Number) property.getValue();
        if (number == null) return null;
        return number.longValue();
    }

    public static void normalizeFeature(SimpleFeature feature, Normalisation normalisation) {
        if (feature.getDefaultGeometry() == null) {
            return;
        }
        Geometry geometry = (Geometry)feature.getDefaultGeometry();
        switch (normalisation) {
        case CLOCKWISE:
            makeClockwise(geometry);
            feature.setDefaultGeometry(geometry);
            break;
        case FULL:
            feature.setDefaultGeometry(geometry.norm());
            break;
        case NONE:
        default:
            break;
        }
    }
    private static void makeClockwise(Geometry geometry) {
        switch (geometry.getGeometryType()) {
        case "Polygon":
            makeClockwise((Polygon) geometry);
            break;
        case "MultiPolygon":
            MultiPolygon mpg = (MultiPolygon) geometry;
            for (int i=0; i < mpg.getNumGeometries(); i++) {
                makeClockwise((Polygon)mpg.getGeometryN(i));
            }
            break;
        default: return;
        }
    }

    /**
     * Make sure the outer ring of the polygon is clockwise
     * and the inner rings are counter-clockwise.
     * The Polygon is modified internally. If this is not desired,
     * make sure to clone the polygon beforehand.
     *
     * @param geometry
     */
    private static void makeClockwise(Polygon polygon) {
        makeClockwise((LinearRing) polygon.getExteriorRing(), true);
        for (int i = 0; i<polygon.getNumInteriorRing(); i++) {
            makeClockwise((LinearRing) polygon.getInteriorRingN(i), false);
        }
        return;
    }

    /**
     * Order the ring's point clockwise if 'clockwise' is true.
     * counter clockwise otherwise;
     * @param ring
     * @param clockwise
     */
    private static void makeClockwise(LinearRing ring, boolean clockwise) {
        if (ring.isEmpty()) {
            return;
        }
        if (CGAlgorithms.isCCW(ring.getCoordinates()) == clockwise) {
            CoordinateArrays.reverse(ring.getCoordinates());
        }

    }

    public static Geometry getGeometry(SimpleFeature feature) {
        return (Geometry) feature.getDefaultGeometry();
    }
}
