package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.openstreetmap.josm.Main;
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
                activeLayer = Main.map.mapView.getActiveLayer();
            }
            if (activeLayer != null) {
                Main.map.mapView.setActiveLayer(activeLayer);
            }
        }
    }

    // private void enableModule(OdsModuleConfig module) {
    // JMenu menu = ODS.getMenu();
    // if (!checkUser(module)) {
    // int answer = JOptionPane.showConfirmDialog(Main.parent,
    // "Je gebruikersnaam eindigt niet op _BAG en is daarom niet geschikt " +
    // "voor de BAG import.\nWeet je zeker dat je door wilt gaan?",
    // I18n.tr("Invalid user"), JOptionPane.OK_CANCEL_OPTION,
    // JOptionPane.INFORMATION_MESSAGE);
    // if (answer == JOptionPane.CANCEL_OPTION) {
    // return;
    // }
    // }
    // module.enable();
    // putValue(Action.NAME, "Disable ODS");
    // menu.setText("ODS "+ module.getName());
    // for (int i=1; i<menu.getItemCount(); i++) {
    // menu.getItem(i).setEnabled(true);
    // }
    // menu.repaint();
    // }

    // private void disableModule(OdsModuleConfig module) {
    // JMenu menu = ODS.getMenu();
    // module.disable();
    // putValue(Action.NAME, "Enable ODS");
    // menu.setText("ODS "+ module.getName());
    // for (int i=1; i<menu.getItemCount(); i++) {
    // menu.getItem(i).setEnabled(false);
    // }
    // }
}
