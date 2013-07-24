package org.openstreetmap.josm.plugins.openservices;

/**
 * An initialization Exception is thrown when something goes wrong in the initialization fase
 * of an object;
 * 
 * @author Gertjan Idema
 *
 */
public class InitializationException extends Exception {

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
