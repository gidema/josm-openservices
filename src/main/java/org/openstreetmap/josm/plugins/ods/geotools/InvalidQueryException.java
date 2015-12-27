package org.openstreetmap.josm.plugins.ods.geotools;

import org.openstreetmap.josm.plugins.ods.InitializationException;

public class InvalidQueryException extends InitializationException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public InvalidQueryException(String message) {
        super(message);
    }

    public InvalidQueryException(Throwable cause) {
        super(cause);
    }

    public InvalidQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
