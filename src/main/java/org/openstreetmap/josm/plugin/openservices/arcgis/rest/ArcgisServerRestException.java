package org.openstreetmap.josm.plugin.openservices.arcgis.rest;

public class ArcgisServerRestException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -6106853460949631324L;

  public ArcgisServerRestException(String message) {
    super(message);
  }

  public ArcgisServerRestException(Throwable cause) {
    super(cause);
  }

  public ArcgisServerRestException(String message, Throwable cause) {
    super(message, cause);
  }

}
