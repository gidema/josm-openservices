package exceptions;

import org.openstreetmap.josm.plugins.ods.io.Host;

public class UnavailableHostException extends OdsException {
    private Host host;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public UnavailableHostException(Host host, Throwable cause) {
        super(cause);
        this.host = host;
    }

    public UnavailableHostException(Host host, String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        String msg = (getCause() == null ? getMessage() : getCause().getMessage());
        return String.format("Host {0} ({1}) is not available: {2}",
            host.getName(),
            host.getUrl().toString(),
            msg);
    }

    
}
