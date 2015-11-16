package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.matching.update.OdsUpdater;

public class OdsUpdateAction extends OdsAction {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OdsUpdateAction(OdsModule module) {
        super(module, "Update", (String)null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO retrieve updater from module context
        OdsUpdater updater = new OdsUpdater(getModule());
        Layer layer = Main.map.mapView.getActiveLayer();
        LayerManager layerManager = getModule().getLayerManager(layer);
        // This action should only occur when the OpenData layer is active
        assert (layerManager != null && !layerManager.isOsm());
        
        OsmDataLayer osmLayer = (OsmDataLayer) layer;
        updater.doUpdate(osmLayer.data.getAllSelected());
    }

    @Override
    public void activeLayerChange(Layer oldLayer, Layer newLayer) {
        LayerManager layerManager = getModule().getLayerManager(newLayer);
        this.setEnabled(layerManager != null && !layerManager.isOsm());
    }
}
