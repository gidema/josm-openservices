package org.openstreetmap.josm.plugins.ods.entities.osm;

import org.openstreetmap.josm.plugins.ods.AbstractLayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;

/**
 * The OsmLayerManager manager the layer containing the data that has been
 * down loaded from the OSM server. 
 * 
 * @author Gertjan Idema
 * 
 */
public class OsmLayerManager extends AbstractLayerManager {
    private OsmEntitiesBuilder entitiesBuilder;

    public OsmLayerManager(OdsModule module, String name) {
        super(name);
        this.entitiesBuilder = new OsmEntitiesBuilder(module);
    }

    @Override
    public boolean isOsm() {
        return true;
    }
    
    public OsmEntitiesBuilder getEntitiesBuilder() {
        return entitiesBuilder;
    }
}
