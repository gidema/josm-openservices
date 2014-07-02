package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.tools.I18n;

public class UnknownLayerException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String layerName;

    public UnknownLayerException(String layerName) {
        super();
        this.layerName = layerName;
    }

    @Override
    public String getMessage() {
        return String.format("Layer '%s' does not exist", layerName);
    }

    @Override
    public String getLocalizedMessage() {
        return I18n.tr("Layer '{0}' does not exist", layerName);
    }
    
    
}
