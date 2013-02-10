package org.openstreetmap.josm.plugins.openservices.wfs;

import org.openstreetmap.josm.plugins.openservices.ServiceException;

public class WfsException extends ServiceException {

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
