package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.matching.update.OdsImporter;
import org.openstreetmap.josm.plugins.ods.matching.update.OdsUpdater;

public class OdsUpdateAction extends OdsAction {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final OsmLayerManager osmLayerManager;
    private final OdLayerManager odLayerManager;

    private final OdsImporter importer;
    private final OdsUpdater updater;

    public OdsUpdateAction(OsmLayerManager osmLayerManager, OdLayerManager odLayerManager,
            OdsImporter importer, OdsUpdater updater) {
        super("Update", (String)null);
        this.osmLayerManager = osmLayerManager;
        this.odLayerManager = odLayerManager;
        this.importer = importer;
        this.updater = updater;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Layer layer = MainApplication.getLayerManager().getActiveLayer();
        // This action should only occur when the OpenData layer is active
        assert layer != null;
        OsmDataLayer osmLayer = (OsmDataLayer) layer;
        if (layer.equals(odLayerManager.getOsmDataLayer())) {
            importer.doImport(osmLayer.getDataSet().getAllSelected());
            osmLayer.getDataSet().clearSelection();
        }
        else if (layer.equals(osmLayerManager.getOsmDataLayer())) {
            updater.doUpdate(osmLayer.getDataSet().getAllSelected());
            osmLayer.getDataSet().clearSelection();
        }
        //        MainApplication.getLayerManager().setActiveLayer(getModule().getOsmLayerManager().getOsmDataLayer());
    }

    @Override
    public void activeLayerChange(Layer oldLayer, Layer newLayer) {
        if (newLayer != null) {
            assert odLayerManager != null;
            this.setEnabled(newLayer.equals(odLayerManager.getOsmDataLayer()) ||
                    newLayer.equals(osmLayerManager.getOsmDataLayer()));
        }
    }
}
