package org.openstreetmap.josm.plugins.ods.entities.osm;

import java.util.List;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;

public class OsmEntitiesBuilder {
    private OdsModule module;

    public OsmEntitiesBuilder(OdsModule module) {
        super();
        this.module = module;
    }
    
    public void build() {
        List<OsmEntityBuilder<?>> entityBuilders = module.getEntityBuilders();
        LayerManager layerManager = module.getOsmLayerManager();
        OsmDataLayer dataLayer = layerManager.getOsmDataLayer();
        if (dataLayer == null) return;
        for (OsmPrimitive primitive : dataLayer.data.allPrimitives()) {
            for (OsmEntityBuilder<?> builder : entityBuilders) {
                builder.buildOsmEntity(primitive);
            }
        }
    }
}
