package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
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
            if (Main.map != null) {
                activeLayer = Main.getLayerManager().getActiveLayer();
            }
            if (activeLayer != null) {
                Main.getLayerManager().setActiveLayer(activeLayer);
            }
            try {
                Bounds bounds = new Bounds(
                    Main.pref.get("openservices.download.bounds"), ";");
                // Zoom to the last used bounds
                Main.map.mapView.zoomTo(bounds);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }
}
