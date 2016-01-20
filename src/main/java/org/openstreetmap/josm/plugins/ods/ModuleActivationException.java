package org.openstreetmap.josm.plugins.ods;

public class ModuleActivationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ModuleActivationException() {
    }

    public ModuleActivationException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public ModuleActivationException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public ModuleActivationException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public ModuleActivationException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }
    
    public static ModuleActivationException CANCELLED = new ModuleActivationException();

}
