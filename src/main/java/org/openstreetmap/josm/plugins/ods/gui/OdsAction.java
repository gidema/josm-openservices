package org.openstreetmap.josm.plugins.ods.gui;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;

public abstract class OdsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    private final OdsContext context;
    
    public OdsAction(OdsContext context, String name, String description) {
        super(name);
        super.putValue("description", description);
        this.context = context;
    }

    public OdsAction(OdsContext context, String name, ImageIcon imageIcon) {
        super(name, imageIcon);
        this.context = context;
    }

    public OdsContext getContext() {
        return context;
    }

    @SuppressWarnings("unused")
    public void activeLayerChange(Layer oldLayer, Layer newLayer) {
        // Override if the implementing action wants to know about this event.
    }
}
