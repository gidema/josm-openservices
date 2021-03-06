package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.matching.update.OdsImporter;
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
        // TODO retrieve updater and importer from module context
        OdsImporter importer = new OdsImporter(getModule());
        OdsUpdater updater = new OdsUpdater(getModule());

        Layer layer = MainApplication.getLayerManager().getActiveLayer();
        LayerManager layerManager = getModule().getLayerManager(layer);
        // This action should only occur when the OpenData layer is active
        assert (layerManager != null && !layerManager.isOsm());
        
        OsmDataLayer osmLayer = (OsmDataLayer) layer;
        importer.doImport(osmLayer.getDataSet().getAllSelected());
        updater.doUpdate(osmLayer.getDataSet().getAllSelected());
        layerManager.getOsmDataLayer().getDataSet().clearSelection();
        MainApplication.getLayerManager().setActiveLayer(getModule().getOsmLayerManager().getOsmDataLayer());
    }

    @Override
    public void activeLayerChange(Layer oldLayer, Layer newLayer) {
        LayerManager layerManager = getModule().getLayerManager(newLayer);
        this.setEnabled(layerManager != null && !layerManager.isOsm());
    }
}
