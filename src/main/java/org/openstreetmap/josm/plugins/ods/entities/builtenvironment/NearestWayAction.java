package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.awt.event.ActionEvent;
import java.util.Collection;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.WaySegment;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.gui.OdsAction;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Pair;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

/**
 * Given a building, find the nearest way
 * 
 * @author Gertjan Idema
 * 
 */
public class NearestWayAction extends OdsAction {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private OdsModule module;
    
    public NearestWayAction(OdsModule module) {
        super(module, I18n.tr("Nearest way"), I18n.tr("Nearest way"));
        this.module = module;
    }


    
    @Override
    public void actionPerformed(ActionEvent e) {
        final OsmDataLayer osmDataLayer = module.getOsmLayerManager().getOsmDataLayer();
        Collection<Way> selected = MainApplication.getLayerManager().getEditLayer().getDataSet()
                .getSelectedWays();
        OsmPrimitive building = selected.iterator().next();
        Node center = new Node(building.getBBox().getCenter());
        WaySegment nearestWaySegment = nearestWaySegment(osmDataLayer.getDataSet(), center);
        MainApplication.getLayerManager().setActiveLayer(osmDataLayer);
        if (nearestWaySegment != null) {
            osmDataLayer.getDataSet().setSelected(nearestWaySegment.getWay());
        }
    }

    private WaySegment nearestWaySegment(DataSet dataSet, Node node) {
        GeoUtil geoUtil = GeoUtil.getInstance();
        Double minDistance = Double.POSITIVE_INFINITY;
        WaySegment nearestWaySegment = null;
        Coordinate coord = GeoUtil.toCoordinate(node);
        for (Way way : dataSet.getWays()) {
            if (!validWay(way))
                continue;
            int i=0;
            for (Pair<Node, Node> pair: way.getNodePairs(false)) {
                LineSegment lineSegment = geoUtil.toSegment(pair);
                Double distance = lineSegment.distance(coord);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestWaySegment = new WaySegment(way, i);
                }
                i++;
            }
        }
        return nearestWaySegment;
    }

    @SuppressWarnings("static-method")
    private boolean validWay(Way way) {
        return way.hasKey("highway") || way.hasKey("water") || way.hasKey("rail");
    }
}
