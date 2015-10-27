package org.openstreetmap.josm.plugins.ods.entities.osm;

import org.openstreetmap.josm.plugins.ods.AbstractLayerManager;

/**
 * The InternalDataLayer is the layer for the data that has been
 * down loaded from the OSM server. 'Internal' means that the data is
 * internal from an OSM point of view as opposed to 'External' data
 * that has been imported from an external source. 
 * 
 * @author Gertjan Idema
 * 
 */
public class InternalDataLayer extends AbstractLayerManager {
    public InternalDataLayer(String name) {
        super(name);
    }

    @Override
    public boolean isOsm() {
        return false;
    }
}
