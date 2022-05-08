package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;
import java.util.List;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.matching.update.OdsImporter;
import org.openstreetmap.josm.plugins.ods.matching.update.OdsUpdater;

public class OdsUpdateAction extends OdsAction {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OdsUpdateAction(OdsContext context) {
        super(context, "Update", (String)null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO retrieve updater and importer from module context
        OdsImporter importer = new OdsImporter(getContext());
        OdsUpdater updater = new OdsUpdater(getContext());

        Layer layer = MainApplication.getLayerManager().getActiveLayer();
        OdLayerManager odLayerManager = getContext().getComponent(OdLayerManager.class);
        OsmLayerManager osmLayerManager = getContext().getComponent(OsmLayerManager.class);
        // This action should only occur when the OpenData layer is active
        assert (odLayerManager != null);
        
        OsmDataLayer osmLayer = (OsmDataLayer) layer;
        importer.doImport(osmLayer.getDataSet().getAllSelected());
        updater.doUpdate(osmLayer.getDataSet().getAllSelected());
        odLayerManager.getOsmDataLayer().getDataSet().clearSelection();
        MainApplication.getLayerManager().setActiveLayer(osmLayerManager.getOsmDataLayer());
    }

    @Override
    public void activeLayerChange(Layer oldLayer, Layer newLayer) {
        List<LayerManager> layerManagers = getContext().getComponents(LayerManager.class);
        layerManagers.forEach(layerManager -> {
            if (layerManager.getOsmDataLayer() == newLayer) {
                this.setEnabled(!layerManager.isOsm());
            }
        });
    }
}
