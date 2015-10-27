package org.openstreetmap.josm.plugins.ods.entities.opendata;

import org.openstreetmap.josm.plugins.ods.AbstractLayerManager;

/**
 * The OpenDataLayerManager manages the layer containing the data that has been
 * imported from an open data source.
 * As opposed to the OsmDataLayerManager that manages data from
 * the OSM server.
 * 
 * @author Gertjan Idema
 * 
 */
public class OpenDataLayerManager extends AbstractLayerManager {

    public OpenDataLayerManager(String name) {
        super(name);
    }

    @Override
    public boolean isOsm() {
        return false;
    }
}
