package org.openstreetmap.josm.plugins.ods;

/**
 * An initialization Exception is thrown when something goes wrong in the initialization fase
 * of an object;
 * 
 * @author Gertjan Idema
 *
 */
public class InitializationException extends Exception {

  /**
     * 
     */
    private static final long serialVersionUID = 1L;

public InitializationException() {
    super();
  }

  public InitializationException(String message, Throwable cause) {
    super(message, cause);
  }

  public InitializationException(String message) {
    super(message);
  }

  public InitializationException(Throwable cause) {
    super(cause);
  }
  
}
