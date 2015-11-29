package org.openstreetmap.josm.plugins.ods.builtenvironment.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.gui.OdsAction;
import org.openstreetmap.josm.tools.I18n;

// Kept for reference purposes
@Deprecated
public class AlignBuildingsAction extends OdsAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public AlignBuildingsAction(OdsModule module) {
        super(module, "Align buildings", "Align buildings");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DataSet ds = Main.main.getCurrentDataSet();
        Collection<OsmPrimitive> primitives = ds.getAllSelected();
        OsmPrimitive building1 = null;
        OsmPrimitive building2 = null;
        if (primitives.size() == 2) {
            building1 = getBuilding(primitives, 0);
            building2 = getBuilding(primitives, 1);
        }
        if (building1 == null || building2 == null) {
            new Notification(I18n.tr("Please select 2 buildings.")).show();
            return;
        }
//        BuildingAligner aligner = new BuildingAligner(0.05, true);
//        aligner.align(building1, building2);
    }

    private static OsmPrimitive getBuilding(Collection<OsmPrimitive> primitives, int i) {
        if (i > primitives.size() - 1) return null;
        OsmPrimitive osm = (OsmPrimitive) primitives.toArray()[i];
        if (osm.getType() == OsmPrimitiveType.NODE) return null;
        if (osm.hasKey("building") || osm.hasKey("building:part")) {
            return osm;
        }
        return null;
    }
}
