package org.openstreetmap.josm.plugins.ods.io;

public enum ServiceState {
    UNINITIALIZED, // The service has not been initialised
    INITIALIZED, // The service has been initialised successfully
    UNAVAILABLE; // An attempt to initialise the service failed. The service is unavailable.
    
    private Throwable cause;

    public Throwable getCause() {
        return cause;
    }
}
