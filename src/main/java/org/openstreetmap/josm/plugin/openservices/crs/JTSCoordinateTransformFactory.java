package org.openstreetmap.josm.plugin.openservices.crs;


/**
 * Factory for JTSCoordinateTransform objects. It't up to the implementation whether
 * the object are being cached;
 * 
 * @author Gertjan Idema
 *
 */
public interface JTSCoordinateTransformFactory {

  /**
   * Create a JTSCoordinateTransform object with default (maximum) precision
   * @param sourceSRID 
   * @param targetSRID 
   * @return 
   * @see createCRSUtil(int,int,Double)
   */
  public JTSCoordinateTransform createJTSCoordinateTransform(int sourceSRID, int targetSRID);

  /**
   * Create a new JTSCoordinateTransform object for the given source and target CRS codes.
   * @param sourceCRSCode
   * @param targetCRSCode
   * @param scale  
   * @return
   */
  public JTSCoordinateTransform createJTSCoordinateTransform(int sourceSRID, int targetSRID, Double scale);
}
