package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.plugins.ods.metadata.MetaDataException;

public class OdsConfigurationException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Type type;
    private String value;
    private Throwable cause;
    
    public OdsConfigurationException(Type type, String value) {
        this.type = type;
        this.value = value;
    }
    
    public OdsConfigurationException(Type type, MetaDataException e) {
        this.type = type;
        this.cause = e;
    }

    public static enum Type {
        UnknownFeatureType,
        InvalidUrl,
        InvalidMetadata,
        HostUnavailable
    }
    
    public String getMessage() {
        if (cause != null) {
            return cause.getMessage();
        }
        switch (type) {
        case UnknownFeatureType:
            return String.format("Unknown feature type: {0}", value);
        case InvalidUrl:
            return String.format("Invalid url: {0}", value);
        case InvalidMetadata:
            return String.format("Invalid metadata", value);
        case HostUnavailable:
            return String.format("The", value);
        }
        return null;
    }
}
