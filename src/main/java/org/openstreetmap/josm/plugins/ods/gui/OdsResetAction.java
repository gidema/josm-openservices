package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.tools.I18n;

public class OdsResetAction extends OdsAction {

    private static final long serialVersionUID = 1L;
    private final OdsContext context;

    public OdsResetAction(OdsContext context) {
        super(context, "Reset", "Reset");
        super.putValue("description",
                "Reset ODS.");
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean reset = true;
        if (context.getComponent(OsmLayerManager.class).getOsmDataLayer().requiresUploadToServer()) {
            String title = I18n.tr("Are you sure?");
            String message = I18n.tr(
                "There are unsaved changes on the OSM layer.\n" +
                "Are you sure you want to reset and lose these changes?");
            int answer = JOptionPane.showOptionDialog(MainApplication.getMainPanel(), message, title, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.CANCEL_OPTION);
            reset = (answer == JOptionPane.OK_OPTION);
        }
        if (reset) {
            context.reset();
        }
    }
}
