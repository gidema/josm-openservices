package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OpenDataServices;
import org.openstreetmap.josm.tools.I18n;

public class OdsEnableAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final OpenDataServices ods;

    public OdsEnableAction(OpenDataServices ods) {
        super("Enable ODS");
        super.putValue("description", "Switch ODS between enabled and disabled state");
        this.ods = ods;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JMenu menu = ods.getMenu();
        OdsModule module = ods.getActiveModule();
        if (module == null) {
            List<OdsModule> modules = ods.getModules();
            if (modules.isEmpty()) {
                JOptionPane.showMessageDialog(Main.parent, 
                    I18n.tr("No ODS module is available."),
                    I18n.tr("Missing module"), JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            module = modules.get(0);
            ods.activate(module);
            putValue(Action.NAME, "Disable ODS");
            menu.setText("ODS "+ module.getName());
            menu.repaint();
        }
        else {
            ods.deactivate(module);
            putValue(Action.NAME, "Enable ODS");
            menu.setText("ODS "+ module.getName());
        }
        
    	Layer activeLayer = null;
    	if (Main.map != null) {
    		activeLayer = Main.map.mapView.getActiveLayer();
    	}
        if (activeLayer != null) {
            Main.map.mapView.setActiveLayer(activeLayer);
        }
    }
    
//    private void enableModule(OdsModuleConfig module) {
//        JMenu menu = ODS.getMenu();
//        if (!checkUser(module)) {
//            int answer = JOptionPane.showConfirmDialog(Main.parent, 
//                 "Je gebruikersnaam eindigt niet op _BAG en is daarom niet geschikt " +
//                 "voor de BAG import.\nWeet je zeker dat je door wilt gaan?",
//                I18n.tr("Invalid user"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
//            if (answer == JOptionPane.CANCEL_OPTION) {
//                return;
//            }
//        }
//        module.enable();
//        putValue(Action.NAME, "Disable ODS");
//        menu.setText("ODS "+ module.getName());
//        for (int i=1; i<menu.getItemCount(); i++) {
//            menu.getItem(i).setEnabled(true);
//        }
//        menu.repaint();
//    }

//    private void disableModule(OdsModuleConfig module) {
//        JMenu menu = ODS.getMenu();
//        module.disable();
//        putValue(Action.NAME, "Enable ODS");
//        menu.setText("ODS "+ module.getName());
//        for (int i=1; i<menu.getItemCount(); i++) {
//            menu.getItem(i).setEnabled(false);
//        }
//    }
}
