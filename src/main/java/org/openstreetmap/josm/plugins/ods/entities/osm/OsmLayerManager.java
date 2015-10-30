package org.openstreetmap.josm.plugins.ods.entities.osm;

import org.openstreetmap.josm.plugins.ods.AbstractLayerManager;

/**
 * The OsmLayerManager manager the layer containing the data that has been
 * down loaded from the OSM server. 
 * 
 * @author Gertjan Idema
 * 
 */
public class OsmLayerManager extends AbstractLayerManager {
    public OsmLayerManager(String name) {
        super(name);
    }

    @Override
    public boolean isOsm() {
        return true;
    }
}
