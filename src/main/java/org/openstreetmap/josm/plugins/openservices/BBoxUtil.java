package org.openstreetmap.josm.plugins.openservices;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.plugins.openservices.crs.JTSCoordinateTransform;
import org.openstreetmap.josm.plugins.openservices.crs.JTSCoordinateTransformFactory;
import org.openstreetmap.josm.plugins.openservices.crs.Proj4jCRSUtilFactory;

import com.vividsolutions.jts.geom.Coordinate;

public class BBoxUtil {
  private static final Long JOSM_SRID = 4326L; 
  private final static JTSCoordinateTransformFactory crsTransformFactory = new Proj4jCRSUtilFactory();
  /**
   * Create a ReferencedEnvelope from a Josm bounds object, using the supplied CoordinateReferenceSystem
   * 
   * @param crs
   * @param bounds
   * @return
   */
  public static ReferencedEnvelope createBoundingBox(CoordinateReferenceSystem crs, Bounds bounds) {
    // A bit complex way to find the crs code, but at least it works.
    String crsCode = crs.getIdentifiers().toArray(new ReferenceIdentifier[0])[0].getCode();
    Long targetSRID = Long.parseLong(crsCode);
    JTSCoordinateTransform transform = crsTransformFactory.createJTSCoordinateTransform(JOSM_SRID, targetSRID);

    Coordinate min = getCoordinate(bounds.getMin());
    Coordinate max = getCoordinate(bounds.getMax());
    Coordinate targetMin = transform.transform(min);
    Coordinate targetMax = transform.transform(max);
    return new ReferencedEnvelope(targetMin.x, targetMax.x,
      targetMin.y, targetMax.y, crs);
  }
  
  private static Coordinate getCoordinate(LatLon ll) {
    return new Coordinate(ll.getX(), ll.getY());
  }
}
