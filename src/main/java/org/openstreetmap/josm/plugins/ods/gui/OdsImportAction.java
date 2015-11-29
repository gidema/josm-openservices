package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.matching.update.OdsImporter;
import org.openstreetmap.josm.tools.ImageProvider;

@Deprecated
public class OdsImportAction extends OdsAction {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OdsImportAction(OdsModule module) {
        super(module, "Import", ImageProvider.get("download"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OdsImporter importer = new OdsImporter(getModule());
        Layer layer = Main.map.mapView.getActiveLayer();
        LayerManager layerManager = getModule().getLayerManager(layer);
        // This action should only occur when the OpenData layer is active
        assert (layerManager != null && !layerManager.isOsm());
        
        OsmDataLayer osmLayer = (OsmDataLayer) layer;
        importer.doImport(osmLayer.data.getAllSelected());
        layerManager.getOsmDataLayer().data.clearSelection();
    }

    @Override
    public void activeLayerChange(Layer oldLayer, Layer newLayer) {
        LayerManager layerManager = getModule().getLayerManager(newLayer);
        this.setEnabled(layerManager != null && !layerManager.isOsm());
    }
}
