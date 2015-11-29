package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.openstreetmap.josm.plugins.ods.OpenDataServicesPlugin;

public class OdsDisableAction extends AbstractAction {
    
    /**
     * 
     */
    private final OpenDataServicesPlugin plugin;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OdsDisableAction(OpenDataServicesPlugin plugin) {
        super("Disable");
        this.plugin = plugin;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.plugin.deactivate(this.plugin.getActiveModule());
    }
}