package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.Preferences;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OpenDataServicesPlugin;

public class OdsEnableAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final OpenDataServicesPlugin ods;
    private final OdsModule module;

    public OdsEnableAction(OpenDataServicesPlugin ods, OdsModule module) {
        super(module.getName());
        super.putValue("description",
                "Switch ODS between enabled and disabled state");
        this.ods = ods;
        this.module = module;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ods.activate(module)) {
            Layer activeLayer = null;
            if (MainApplication.getMap() != null) {
                activeLayer = MainApplication.getLayerManager().getActiveLayer();
            }
            if (activeLayer != null) {
                MainApplication.getLayerManager().setActiveLayer(activeLayer);
            }
            try {
                String savedBounds = Preferences.main().get("openservices.download.bounds");
                if (savedBounds != null) {
                    Bounds bounds = new Bounds(savedBounds, ";");
                    MainApplication.getMap().mapView.zoomTo(bounds);
                }
                // Zoom to the last used bounds
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }
}
