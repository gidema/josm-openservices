package nl.gertjanidema.conversion.valuemapper;

public class ValueMapperException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -3527630134958535484L;

  ValueMapperException() {
    super();
    // TODO Auto-generated constructor stub
  }

  ValueMapperException(String format, Throwable cause, Object ...args) {
    super(String.format(format, args), cause);
    // TODO Auto-generated constructor stub
  }

  ValueMapperException(String format, Object ...args) {
    super(String.format(format, args));
  }

  ValueMapperException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }
  
}
