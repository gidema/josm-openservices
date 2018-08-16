package org.openstreetmap.josm.plugins.ods.entities.osm;

import java.util.Collection;
import java.util.List;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;

public class OsmEntitiesBuilder implements Runnable {
    private final OsmLayerManager osmLayerManager;
    private final List<OsmEntityBuilder<?>> entityBuilders;

    public OsmEntitiesBuilder(List<OsmEntityBuilder<?>> entityBuilders, OsmLayerManager osmLayerManager) {
        super();
        this.entityBuilders = entityBuilders;
        this.osmLayerManager = osmLayerManager;
    }

    /**
     * Build ODS entities from OSM primitives.
     * Check all primitives in the OSM layer
     *
     */
    @Override
    public void run() {
        OsmDataLayer dataLayer = osmLayerManager.getOsmDataLayer();
        if (dataLayer == null) return;
        build(dataLayer.getDataSet().allPrimitives());
    }

    /**
     * Build Ods entities from the provided OSM primitives
     *
     * @param osmPrimitives
     */
    public void build(Collection<? extends OsmPrimitive> osmPrimitives) {
        for (OsmPrimitive primitive : osmPrimitives) {
            for (OsmEntityBuilder<?> builder : entityBuilders) {
                builder.buildOsmEntity(primitive);
            }
        }
    }
}
