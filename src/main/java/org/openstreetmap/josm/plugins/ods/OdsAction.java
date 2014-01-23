package org.openstreetmap.josm.plugins.ods;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.UserInfo;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.OsmServerUserInfoReader;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.tools.I18n;

public class OdsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public OdsAction() {
        super("Enable ODS");
        super.putValue("description", "Switch ODS between enabled and disabled state");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OdsModule module = ODS.getModule();
        if (module == null) {
            JOptionPane.showMessageDialog(Main.parent, 
                    I18n.tr("No ODS module is available."),
                I18n.tr("Missing module"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (module.isEnabled()) {
            disableModule(module);
        }
        else {
            enableModule(module);
        }
        
    	Layer activeLayer = null;
    	if (Main.map != null) {
    		activeLayer = Main.map.mapView.getActiveLayer();
    	}
        if (activeLayer != null) {
            Main.map.mapView.setActiveLayer(activeLayer);
        }
    }
    
    private void enableModule(OdsModule module) {
        JMenu menu = ODS.getMenu();
        if (!checkUser(module)) {
            int answer = JOptionPane.showConfirmDialog(Main.parent, 
                 "Je gebruikersnaam eindigt niet op _BAG en is daarom niet geschikt " +
                 "voor de BAG import.\nWeet je zeker dat je door wilt gaan?",
                I18n.tr("Invalid user"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (answer == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        module.enable();
        putValue(Action.NAME, "Disable ODS");
        menu.setText("ODS "+ module.getName());
        for (int i=1; i<menu.getItemCount(); i++) {
            menu.getItem(i).setEnabled(true);
        }
        menu.repaint();
    }

    private void disableModule(OdsModule module) {
        JMenu menu = ODS.getMenu();
        module.disable();
        putValue(Action.NAME, "Enable ODS");
        menu.setText("ODS "+ module.getName());
        for (int i=1; i<menu.getItemCount(); i++) {
            menu.getItem(i).setEnabled(false);
        }
    }

    private boolean checkUser(OdsModule module) {
        try {
            final UserInfo userInfo = new OsmServerUserInfoReader().fetchUserInfo(NullProgressMonitor.INSTANCE);
            String user = userInfo.getDisplayName();
            String suffix = "_" +module.getName();
            return user.endsWith(suffix);
        } catch (OsmTransferException e1) {
            Main.warn(tr("Failed to retrieve OSM user details from the server."));
            return false;
        }
    }
}
