package org.openstreetmap.josm.plugins.ods;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
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
//        super.setDescription("Switch ODS between enabled and disabled state");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OdsModule module = ODS.getModule();
        if (module != null) {
            JOptionPane.showMessageDialog(Main.parent, 
                I18n.tr("ODS has allready been enabled for {0}.", module.getName()),
            I18n.tr("Warning"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Collection<OdsModule> modules = ODS.getModules();
        if (modules.isEmpty()) {
            JOptionPane.showMessageDialog(Main.parent, 
                    I18n.tr("No ODS module is available."),
                I18n.tr("Missing module"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        module = modules.iterator().next();
        if (!checkUser(module)) {
            int answer = JOptionPane.showConfirmDialog(Main.parent, 
                 "Je gebruikersnaam eindigt niet op _BAG en is daarom niet geschikt " +
                 "voor de BAG import.\nWeet je zeker dat je door wilt gaan?",
                I18n.tr("Invalid user"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (answer == 2) {
                // TODO where is the constant for CANCEL?
                return;
            }
        }
        ODS.setModule(module);
        JMenu menu = ODS.getMenu();
        menu.getItem(1).setEnabled(true);
        
    	Layer activeLayer = null;
    	if (Main.map != null) {
    		activeLayer = Main.map.mapView.getActiveLayer();
    	}
        if (activeLayer != null) {
            Main.map.mapView.setActiveLayer(activeLayer);
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
