package org.openstreetmap.josm.plugins.openservices.crs;

/**
 * Exception to be thrown when the Geometry supplied to a JTSCoordinateTransform
 * doesn't have the expected SRID
 * 
 * @author Gertjan Idema
 *
 */
public class InvalidSRIDException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 7913719374412357705L;

  /**
   * Default constructor
   */
  public InvalidSRIDException() {
    super();
  }
}
