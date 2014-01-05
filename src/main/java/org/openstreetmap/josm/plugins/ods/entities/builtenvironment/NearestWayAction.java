package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.awt.event.ActionEvent;
import java.util.Collection;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.WaySegment;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.OdsAction;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.tools.Pair;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

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

    @Override
    public void actionPerformed(ActionEvent e) {
        final OsmDataLayer osmDataLayer = workingSet.getInternalDataLayer().getOsmDataLayer();
        Collection<Way> selected = Main.map.mapView.getEditLayer().data
                .getSelectedWays();
        OsmPrimitive building = selected.iterator().next();
        Node center = new Node(building.getBBox().getCenter());
        WaySegment nearestWaySegment = nearestWaySegment(osmDataLayer.data, center);
        Main.map.mapView.setActiveLayer(osmDataLayer);
        if (nearestWaySegment != null) {
            osmDataLayer.data.setSelected(nearestWaySegment.way);
        }
    }

    private WaySegment nearestWaySegment(DataSet dataSet, Node node) {
        GeoUtil geoUtil = GeoUtil.getInstance();
        Double minDistance = Double.POSITIVE_INFINITY;
        WaySegment nearestWaySegment = null;
        Coordinate coord = geoUtil.toCoordinate(node);
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

    private boolean validWay(Way way) {
        return way.hasKey("highway") || way.hasKey("water") || way.hasKey("rail");
    }
}
