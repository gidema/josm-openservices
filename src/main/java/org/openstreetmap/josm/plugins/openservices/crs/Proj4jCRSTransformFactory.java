package org.openstreetmap.josm.plugins.openservices.crs;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;
import org.osgeo.proj4j.util.CRSCache;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * Proj4j based implementation of JTSCoordinateTransformFactory
 * Caches created Transforms to prevent duplication 
 * 
 * @author Gertjan Idema
 * 
 */
public class Proj4jCRSTransformFactory implements JTSCoordinateTransformFactory {
  static CRSCache crsCache = new CRSCache();
  static CoordinateTransformFactory CTFactory = new CoordinateTransformFactory();
  static Map<String, JTSCoordinateTransform> JTSCoordinateTransformCache = new HashMap<String, JTSCoordinateTransform>();

  @Override
  public JTSCoordinateTransform createJTSCoordinateTransform(Long sourceSRID, Long targetSRID) {
    return createJTSCoordinateTransform(sourceSRID, targetSRID, null);
  }
  
  @Override
  public JTSCoordinateTransform createJTSCoordinateTransform(Long sourceSRID, Long targetSRID, Double scale) {
    String key = String.format(Locale.UK, "%d|%d|%f", sourceSRID, targetSRID, scale);
    JTSCoordinateTransform crsUtil = JTSCoordinateTransformCache.get(key);
    if (crsUtil == null) {
        crsUtil = new CRSUtilImpl(sourceSRID, targetSRID, scale);
      JTSCoordinateTransformCache.put(key, crsUtil);
    }
    return crsUtil;
  }

  /**
   * Implementation of {@link JTSCoordinateTransform} based on proj4j
   * 
   */
  public class CRSUtilImpl extends JTSCoordinateTransform {
    private CoordinateReferenceSystem sourceCrsProj4j;
    private CoordinateReferenceSystem targetCrsProj4j;
    private CoordinateTransform ct;

    public CRSUtilImpl(Long sourceSRID, Long targetSRID) {
      super(sourceSRID, targetSRID);
      init();
    }

    public CRSUtilImpl(Long sourceSRID, Long targetSRID, Double scale) {
      super(sourceSRID, targetSRID, scale);
      init();
    }

    public CRSUtilImpl(Long sourceSRID, Long targetSRID,
        PrecisionModel precisionModel) {
      super(sourceSRID, targetSRID, precisionModel);
      init();
    }

    private void init() {
      String sourceCrsCode = String.format("epsg:%d", getSourceSRID());
      String targetCrsCode = String.format("epsg:%d", getTargetSRID());
      sourceCrsProj4j = crsCache.createFromName(sourceCrsCode);
      targetCrsProj4j = crsCache.createFromName(targetCrsCode);
      ct = CTFactory.createTransform(sourceCrsProj4j, targetCrsProj4j);
    }

    
    @Override
    public JTSCoordinateTransform getReverseTransform() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public Coordinate transform(Coordinate coord) {
      return coordinate(transform(projCoordinate(coord)));
    }

    private ProjCoordinate transform(ProjCoordinate coord) {
      return ct.transform(coord, new ProjCoordinate());
    }

    /**
     * Convert form JTS Coordinate to Proj4j ProjCoordinate 
     * @param coord
     * @return
     */
    private ProjCoordinate projCoordinate(Coordinate coord) {
      return new ProjCoordinate(coord.x, coord.y, coord.z);
    }

    /**
     * Convert from Proj4j ProjCoordinate to JTS Coordinate
     * @param coord
     * @return
     */
    private Coordinate coordinate(ProjCoordinate coord) {
      Coordinate result = new Coordinate(coord.x, coord.y, coord.z);
      if (getPrecisionModel() != null) {
        getPrecisionModel().makePrecise(result);
      }
      return result;
    }
  }
}
