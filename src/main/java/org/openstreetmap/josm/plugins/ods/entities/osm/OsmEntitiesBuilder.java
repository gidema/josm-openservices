package org.openstreetmap.josm.plugins.ods.entities.osm;

import java.util.Collection;
import java.util.List;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.matching.OsmAddressNodeToBuildingMatcher;

public class OsmEntitiesBuilder {
    private final OsmAddressNodeStore addressNodeStore;
    private final OsmAddressNodeToBuildingMatcher nodeToBuildingMatcher;
    private final OsmLayerManager osmLayerManager;
    private final List<OsmEntityBuilder<?>> entityBuilders;

    public OsmEntitiesBuilder(OsmAddressNodeStore addressNodeStore, OsmAddressNodeToBuildingMatcher nodeToBuildingMatcher,
            List<OsmEntityBuilder<?>> entityBuilders, OsmLayerManager osmLayerManager) {
        super();
        this.addressNodeStore = addressNodeStore;
        this.nodeToBuildingMatcher = nodeToBuildingMatcher;
        this.entityBuilders = entityBuilders;
        this.osmLayerManager = osmLayerManager;
    }

    /**
     * Build ODS entities from OSM primitives.
     * Check all primitives in the OSM layer
     *
     */
    public void build() {
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
        if (addressNodeStore != null) {
            addressNodeStore.forEach(nodeToBuildingMatcher::match);
        }
    }
}
