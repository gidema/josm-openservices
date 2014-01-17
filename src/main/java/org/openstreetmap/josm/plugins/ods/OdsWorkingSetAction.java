package org.openstreetmap.josm.plugins.ods;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.layer.Layer;

public class OdsWorkingSetAction extends OdsAction {

    private static final long serialVersionUID = 1L;

    public OdsWorkingSetAction(OdsWorkingSet workingSet) {
        super();
        this.setWorkingSet(workingSet);
        this.setName(workingSet.getName());
        this.setDescription(workingSet.getDescription());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	Layer activeLayer = null;
    	if (Main.map != null) {
    		activeLayer = Main.map.mapView.getActiveLayer();
    	}
        Action action = workingSet.getActivateAction();
        action.actionPerformed(e);
        if (activeLayer != null) {
            Main.map.mapView.setActiveLayer(activeLayer);
        }
    }
}
