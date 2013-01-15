package org.openstreetmap.josm.plugins.openservices.wfs;

public class WfsException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -6106853460949631324L;

  public WfsException(String message) {
    super(message);
  }

  public WfsException(Throwable cause) {
    super(cause);
  }

  public WfsException(String message, Throwable cause) {
    super(message, cause);
  }

}
