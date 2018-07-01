package org.openstreetmap.josm.plugins.ods.gui;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.openstreetmap.josm.gui.layer.Layer;

public abstract class OdsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public OdsAction(String name, String description) {
        super(name);
        super.putValue("description", description);
    }

    public OdsAction(String name, ImageIcon imageIcon) {
        super(name, imageIcon);
    }

    @SuppressWarnings("unused")
    public void activeLayerChange(Layer oldLayer, Layer newLayer) {
        // Override if the implementing action wants to know about this event.
    }
}
