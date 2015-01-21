package org.openstreetmap.josm.plugins.ods.entities.external;

import org.openstreetmap.josm.plugins.ods.AbstractDataLayer;

/**
 * The ExternalDataLayer is the layer for the data that has been
 * imported from an external data source.
 * As opposed to the InternalDataLayer that contains data from
 * the OSM server. 
 * 
 * @author Gertjan Idema
 * 
 */
public class ExternalDataLayer extends AbstractDataLayer {

    public ExternalDataLayer(String name) {
        super(name);
    }

    @Override
    public boolean isInternal() {
        return false;
    }
}
