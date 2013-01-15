package org.openstreetmap.josm.plugin.openservices.crs;

import com.vividsolutions.jts.geom.Coordinate;
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
  private int sourceSRID;
  private int targetSRID;
  private GeometryFactory targetFactory;

  
  /**
   * Create a new JTSCoordinateTransform with a defaults precision model;
   * @param sourceSRID
   * @param targetSRID
   */
  public JTSCoordinateTransform(int sourceSRID, int targetSRID) {
    this(sourceSRID, targetSRID, (Double)null);
  }

  /**
   * Create a new JTSCoordinateTransform having a precision model with the supplied scale;
   * @param sourceSRID
   * @param targetSRID
   * @param scale
   */
  public JTSCoordinateTransform(int sourceSRID, int targetSRID, Double scale) {
    this(sourceSRID, targetSRID, createPrecisionModel(scale));
  }
  
  /**
   * Create a new JTSCoordinateTransform with the supplied precision
   * @param sourceSRID
   * @param targetSRID
   * @param scale
   */
  public JTSCoordinateTransform(int sourceSRID, int targetSRID, PrecisionModel precisionModel) {
    this.sourceSRID = sourceSRID;
    this.targetSRID = targetSRID;
    this.targetFactory = new GeometryFactory(precisionModel, targetSRID);
  }
  
  /**
   * Get the source coordinate system reference ID
   * @return
   */
  public final int getSourceSRID() {
    return sourceSRID;
  }

  /**
   * Get the target coordinate system reference ID
   * @return
   */
  public final int getTargetSRID() {
    return targetSRID;
  }

  /**
   * Get the Geometry factory for the target CRS
   * @return
   */
  public final GeometryFactory getTargetFactory() {
    return targetFactory;
  }

  protected final void setSourceSRID(int sourceSRID) {
    this.sourceSRID = sourceSRID;
  }

  protected final void setTargetSRID(int targetSRID) {
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
  //public abstract ReferencedEnvelope createBoundingBox(Bounds bounds);

  /**
   * Transform a coordinate from the source CRS to the target CRS
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
   * Set the precisionModel used to reduced the precision of the
   * result value 
   * @param precisionModel
   */
  //public abstract void setPrecisionModel(PrecisionModel precisionModel);
  
  /**
   * Transform a coordinate array
   * @param coords
   * @return
   */
  public Coordinate[] transform(Coordinate[] coords) {
    Coordinate[] result = new Coordinate[coords.length];
    for (int i=0; i<coords.length; i++) {
      result[i] = transform(coords[i]);
    }
    return result;
  }
  
  /**
   * Transform a Point object
   * @param point
   * @return
   * @throws InvalidSRIDException
   */
  public Point transform(Point point) throws InvalidSRIDException {
    checkSRID(point);
    return targetFactory.createPoint(transform(point.getCoordinate()));
  }
  
  /**
   * Transform a LineString object
   * @param lineString
   * @return
   * @throws InvalidSRIDException
   */
  public LineString transform(LineString lineString) throws InvalidSRIDException {
    checkSRID(lineString);
    return targetFactory.createLineString(transform(lineString.getCoordinates()));
  }
  
  /**
   * Transform a Polygon object
   * @param polygon
   * @return
   * @throws InvalidSRIDException
   */
  public Polygon transform(Polygon polygon) throws InvalidSRIDException {
    checkSRID(polygon);
    LinearRing shell = (LinearRing) transform(polygon.getExteriorRing());
    LinearRing[] holes = new LinearRing[polygon.getNumInteriorRing()];
    for (int i=0; i<holes.length; i++) {
      holes[i] = (LinearRing) transform(polygon.getInteriorRingN(i));
    }
    return targetFactory.createPolygon(shell, holes);
  }
  
  /**
   * Transform a Geometry object
   * @param geom
   * @return
   * @throws InvalidSRIDException
   */
  public Geometry transform(Geometry geom) throws InvalidSRIDException {
    String type = geom.getGeometryType();
    if (type.equals("POINT")) {
      return transform((Point)geom);
    }
    if (type.equals("LINESTRING")) {
      return transform((LineString)geom);
    }
    if (type.equals("LINEARRING")) {
      return transform((LinearRing)geom);
    }
    if (type.equals("POLYGON")) {
      return transform((Polygon)geom);
    }
    if (type.equals("MULTIPOINT")) {
      return transform((MultiPoint)geom);
    }
    if (type.equals("MULTILINESTRING")) {
      return transform((MultiLineString)geom);
    }
    if (type.equals("MULTIPOLYGON")) {
      return transform((MultiPolygon)geom);
    }
    throw new RuntimeException("Unknown geometry: " + type);
  }

  /**
   * Transform a MultiPoint object
   * @param mp
   * @return
   * @throws InvalidSRIDException
   */
  public MultiPoint transform(MultiPoint mp) throws InvalidSRIDException {
    Point[] result = new Point[mp.getNumGeometries()];
    for (int i=0; i<mp.getNumGeometries(); i++) {
      result[i] = (Point) transform(mp.getGeometryN(i));
    }
    return targetFactory.createMultiPoint(result);
  }
  
  /**
   * Transform a MultiLineString object
   * @param mls
   * @return
   * @throws InvalidSRIDException
   */
  public MultiLineString transform(MultiLineString mls) throws InvalidSRIDException {
    LineString[] result = new LineString[mls.getNumGeometries()];
    for (int i=0; i<mls.getNumGeometries(); i++) {
      result[i] = (LineString) transform(mls.getGeometryN(i));
    }
    return targetFactory.createMultiLineString(result);
  }
  
  /**
   * Transform a MultiPolygon object
   * @param mp
   * @return
   * @throws InvalidSRIDException
   */
  public MultiPolygon transform(MultiPolygon mp) throws InvalidSRIDException {
    Polygon[] result = new Polygon[mp.getNumGeometries()];
    for (int i=0; i<mp.getNumGeometries(); i++) {
      result[i] = (Polygon) transform(mp.getGeometryN(i));
    }
    return targetFactory.createMultiPolygon(result);
  }
  
  /**
   * Transform a GeometryCollection object
   * @param gc
   * @return
   * @throws InvalidSRIDException
   */
  public GeometryCollection transform(GeometryCollection gc) throws InvalidSRIDException {
    checkSRID(gc);
    Geometry[] result = new Geometry[gc.getNumGeometries()];
    for (int i=0; i<gc.getNumGeometries(); i++) {
      result[i] = transform(gc.getGeometryN(i));
    }
    return targetFactory.createGeometryCollection(result);
  }
  
  /**
   * Check if the geometry has the expected SRID and throw an
   * InvalidSRIDException if not
   * @param geometry
   * @throws InvalidSRIDException
   */
  private void checkSRID(Geometry geometry) throws InvalidSRIDException {
    if (geometry.getSRID() != sourceSRID) {
      throw new InvalidSRIDException();
    }
  }
}
