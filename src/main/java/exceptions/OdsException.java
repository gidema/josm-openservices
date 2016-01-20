package exceptions;

public class OdsException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OdsException(String message) {
        super(message);
    }

    public OdsException(Throwable cause) {
        super(cause);
    }

    public OdsException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
}
