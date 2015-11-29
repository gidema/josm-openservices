package org.openstreetmap.josm.plugins.ods.crs;

public class CRSException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 5083461688038853504L;

    public CRSException(String message) {
        super(message);
    }

    public CRSException(Throwable cause) {
        super(cause);
    }

    public CRSException(String message, Throwable cause) {
        super(message, cause);
    }
}
