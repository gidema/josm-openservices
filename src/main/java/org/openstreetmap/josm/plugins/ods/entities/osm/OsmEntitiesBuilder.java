package org.openstreetmap.josm.plugins.ods.entities.osm;

import java.util.Collection;
import java.util.List;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.matching.OsmAddressNodeToBuildingMatcher;

public class OsmEntitiesBuilder {
    private OdsModule module;
    private OsmAddressNodeToBuildingMatcher nodeToBuildingMatcher;

    public OsmEntitiesBuilder(OdsModule module) {
        super();
        this.module = module;
        this.nodeToBuildingMatcher = new OsmAddressNodeToBuildingMatcher(module);
    }
    
    /**
     * Build ODS entities from OSM primitives.
     * Check all primitives in the OSM layer
     * 
     */
    public void build() {
        LayerManager layerManager = module.getOsmLayerManager();
        OsmDataLayer dataLayer = layerManager.getOsmDataLayer();
        if (dataLayer == null) return;
        build(dataLayer.getDataSet().allPrimitives());
    }
    
    /**
     * Build Ods entities from the provided OSM primitives
     * 
     * @param osmPrimitives
     */
    public void build(Collection<? extends OsmPrimitive> osmPrimitives) {
        List<OsmEntityBuilder<?>> entityBuilders = module.getEntityBuilders();
        for (OsmPrimitive primitive : osmPrimitives) {
            for (OsmEntityBuilder<?> builder : entityBuilders) {
                builder.buildOsmEntity(primitive);
            }
        }
        OsmLayerManager layerManager = module.getOsmLayerManager();
        EntityStore<AddressNode> addressNodeStore = layerManager.getEntityStore(AddressNode.class);
        if (addressNodeStore != null) {
            addressNodeStore.forEach(nodeToBuildingMatcher::match);
        }
    }
}
