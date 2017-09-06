package org.openstreetmap.josm.plugins.ods.crs;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * Provides Coordinate transformations for JTS geometries
 * 
 * @author Gertjan Idema
 * 
 */
public abstract class JTSCoordinateTransform {
    private Long sourceSRID;
    private Long targetSRID;
    private GeometryFactory targetFactory;

    /**
     * Create a new JTSCoordinateTransform with a defaults precision model;
     * 
     * @param sourceSRID
     * @param targetSRID
     */
    public JTSCoordinateTransform(Long sourceSRID, Long targetSRID) {
        this(sourceSRID, targetSRID, (Double) null);
    }

    /**
     * Create a new JTSCoordinateTransform having a precision model with the
     * supplied scale;
     * 
     * @param sourceSRID
     * @param targetSRID
     * @param scale
     */
    public JTSCoordinateTransform(Long sourceSRID, Long targetSRID, Double scale) {
        this(sourceSRID, targetSRID, createPrecisionModel(scale));
    }

    /**
     * Create a new JTSCoordinateTransform with the supplied precision
     * 
     * @param sourceSRID
     * @param targetSRID
     * @param scale
     */
    public JTSCoordinateTransform(Long sourceSRID, Long targetSRID,
            PrecisionModel precisionModel) {
        this.sourceSRID = sourceSRID;
        this.targetSRID = targetSRID;
        this.targetFactory = new GeometryFactory(precisionModel,
                targetSRID.intValue());
    }

    /**
     * Get the source coordinate system reference ID
     * 
     * @return
     */
    public final Long getSourceSRID() {
        return sourceSRID;
    }

    /**
     * Get the target coordinate system reference ID
     * 
     * @return
     */
    public final Long getTargetSRID() {
        return targetSRID;
    }

    /**
     * Get the Geometry factory for the target crs
     * 
     * @return
     */
    public final GeometryFactory getTargetFactory() {
        return targetFactory;
    }

    protected final void setSourceSRID(Long sourceSRID) {
        this.sourceSRID = sourceSRID;
    }

    protected final void setTargetSRID(Long targetSRID) {
        this.targetSRID = targetSRID;
    }

    protected final void setTargetFactory(GeometryFactory targetFactory) {
        this.targetFactory = targetFactory;
    }

    private static PrecisionModel createPrecisionModel(Double scale) {
        if (scale != null) {
            return new PrecisionModel(scale);
        }
        return new PrecisionModel();
    }

    /**
     * Get the target precision model
     * 
     * @return
     */
    public PrecisionModel getPrecisionModel() {
        return targetFactory.getPrecisionModel();
    }

    /**
     * Create a geotools Referenced envelope from a josm Bounds object.
     * 
     * @param bounds
     * @return the envelope
     */
    // public abstract ReferencedEnvelope createBoundingBox(Bounds bounds);

    /**
     * Transform a coordinate from the source crs to the target crs
     * 
     * @param coord
     * @return the transformed coordinate
     */
    public abstract Coordinate transform(Coordinate coord);

    /**
     * Get the coordinate transformation that does the reverse transformation
     * 
     * @return the reverse transformation
     */
    public abstract JTSCoordinateTransform getReverseTransform();

    /**
     * Set the precisionModel used to reduced the precision of the result value
     * 
     * @param precisionModel
     */
    // public abstract void setPrecisionModel(PrecisionModel precisionModel);

    /**
     * Transform a coordinate array
     * 
     * @param coords
     * @return
     */
    public Coordinate[] transform(Coordinate[] coords) {
        Coordinate[] result = new Coordinate[coords.length];
        for (int i = 0; i < coords.length; i++) {
            result[i] = transform(coords[i]);
        }
        return result;
    }

    /**
     * Transform a Point object
     * 
     * @param point
     * @return
     */
    public Point transform(Point point) {
        return targetFactory.createPoint(transform(point.getCoordinate()));
    }

    /**
     * Transform a LineString object
     * 
     * @param lineString
     * @return
     */
    public LineString transform(LineString lineString) {
        return targetFactory.createLineString(transform(lineString
                .getCoordinates()));
    }

    /**
     * Transform a LinearRing object
     * 
     * @param lineString
     * @return
     */
    public LinearRing transform(LinearRing linearRing) {
        return targetFactory.createLinearRing(transform(linearRing.getCoordinates()));
    }

    /**
     * Transform a Polygon object
     * 
     * @param polygon
     * @return
     */
    public Polygon transform(Polygon polygon) {
        LinearRing shell = transform((LinearRing)polygon.getExteriorRing());
        LinearRing[] holes = new LinearRing[polygon.getNumInteriorRing()];
        for (int i = 0; i < holes.length; i++) {
            holes[i] = transform((LinearRing)polygon.getInteriorRingN(i));
        }
        return targetFactory.createPolygon(shell, holes);
    }

    /**
     * Transform an Envelope object
     * 
     * @param envelope
     * @return
     */
    public Envelope transform(Envelope envelope) {
        Double minX = envelope.getMinX();
        Double maxX = envelope.getMaxX();
        Double minY = envelope.getMinY();
        Double maxY = envelope.getMaxY();
        Coordinate min = transform(new Coordinate(minX, minY));
        Coordinate max = transform(new Coordinate(maxX, maxY));
        return new Envelope(min, max);
    }

    /**
     * Transform a Geometry object
     * 
     * @param geom
     * @return
     */
    public Geometry transform(Geometry geom) {
        String type = geom.getGeometryType().toUpperCase();
        if (type.equals("POINT")) {
            return transform((Point) geom);
        }
        if (type.equals("LINESTRING")) {
            return transform((LineString) geom);
        }
        if (type.equals("LINEARRING")) {
            return transform((LinearRing) geom);
        }
        if (type.equals("POLYGON")) {
            return transform((Polygon) geom);
        }
        if (type.equals("MULTIPOINT")) {
            return transform((MultiPoint) geom);
        }
        if (type.equals("MULTILINESTRING")) {
            return transform((MultiLineString) geom);
        }
        if (type.equals("MULTIPOLYGON")) {
            return transform((MultiPolygon) geom);
        }
        throw new RuntimeException("Unknown geometry: " + type);
    }

    /**
     * Transform a MultiPoint object
     * 
     * @param mp
     * @return
     */
    public MultiPoint transform(MultiPoint mp) {
        Point[] result = new Point[mp.getNumGeometries()];
        for (int i = 0; i < mp.getNumGeometries(); i++) {
            result[i] = (Point) transform(mp.getGeometryN(i));
        }
        return targetFactory.createMultiPoint(result);
    }

    /**
     * Transform a MultiLineString object
     * 
     * @param mls
     * @return
     */
    public MultiLineString transform(MultiLineString mls) {
        LineString[] result = new LineString[mls.getNumGeometries()];
        for (int i = 0; i < mls.getNumGeometries(); i++) {
            result[i] = (LineString) transform(mls.getGeometryN(i));
        }
        return targetFactory.createMultiLineString(result);
    }

    /**
     * Transform a MultiPolygon object
     * 
     * @param mp
     * @return
     */
    public MultiPolygon transform(MultiPolygon mp) {
        Polygon[] result = new Polygon[mp.getNumGeometries()];
        for (int i = 0; i < mp.getNumGeometries(); i++) {
            result[i] = (Polygon) transform(mp.getGeometryN(i));
        }
        return targetFactory.createMultiPolygon(result);
    }

    /**
     * Transform a GeometryCollection object
     * 
     * @param gc
     * @return
     */
    public GeometryCollection transform(GeometryCollection gc) {
        Geometry[] result = new Geometry[gc.getNumGeometries()];
        for (int i = 0; i < gc.getNumGeometries(); i++) {
            result[i] = transform(gc.getGeometryN(i));
        }
        return targetFactory.createGeometryCollection(result);
    }
}
