package org.openstreetmap.josm.plugins.ods.builtenvironment.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.gui.OdsAction;
import org.openstreetmap.josm.plugins.ods.osm.SmallSegmentRemover;
import org.openstreetmap.josm.tools.I18n;

public class RemoveShortSegmentsAction extends OdsAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public RemoveShortSegmentsAction(OdsContext context) {
        super(context, "Remove short segments", "Remove short segments");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DataSet ds = MainApplication.getLayerManager().getEditDataSet();
        Collection<Way> ways = ds.getSelectedWays();
        if (ways.size() != 1) {
            new Notification(I18n.tr("Select 1 way.")).show();
            return;
        }
        Way way = ways.iterator().next();
        SmallSegmentRemover.removeSmallSegments(way, 0.05, true);
    }
}
