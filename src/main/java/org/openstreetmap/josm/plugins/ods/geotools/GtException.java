package org.openstreetmap.josm.plugins.ods.geotools;

import org.openstreetmap.josm.plugins.ods.ServiceException;

public class GtException extends ServiceException {

  /**
   * 
   */
  private static final long serialVersionUID = -6106853460949631324L;

  public GtException(String message) {
    super(message);
  }

  public GtException(Throwable cause) {
    super(cause);
  }

  public GtException(String message, Throwable cause) {
    super(message, cause);
  }

}
