package org.openstreetmap.josm.plugins.openservices.buildings;

import java.awt.event.ActionEvent;
import java.util.Collection;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.openservices.OdsAction;

/**
 * Given a building, find the nearest way
 * 
 * @author Gertjan Idema
 *
 */
public class NearestWayAction extends OdsAction {

  @Override
  public void actionPerformed(ActionEvent e) {
    final OsmDataLayer osmDataLayer = workingSet.getOdsOsmDataLayer();
    Collection<Way> selected = Main.map.mapView.getEditLayer().data.getSelectedWays();
    OsmPrimitive building = selected.iterator().next();
    Node node = anyNode(building);
    Way nearestWay = nearestWay(osmDataLayer.data, node);
    Main.map.mapView.setActiveLayer(osmDataLayer);
    if (nearestWay != null) {
      osmDataLayer.data.setSelected(nearestWay);
    }
  }
  
  private Node anyNode(OsmPrimitive building) {
    if (building instanceof Way) {
      Way way = (Way) building;
      return way.getNode(0);
    }
    throw new UnsupportedOperationException();
  }
  
  private Way nearestWay(DataSet dataSet, Node node) {
    Double minDistance = Double.POSITIVE_INFINITY;
    Way nearest = null;
    EastNorth en = node.getEastNorth();
    for (Way way : dataSet.getWays()) {
      if (!validWay(way)) continue;
      Double distance = getDistance(way, en);
      if (distance < minDistance) {
        minDistance = distance;
        nearest = way;
      }
    }
    return nearest;
  }
  
  private boolean validWay(Way way) {
    return way.hasKey("highway");
  }

  /**
   * Calculate the distance between a way an a point
   * TODO move to an utility class
   * 
   * @param way
   * @param node
   * @return
   */
  private Double getDistance(Way way, EastNorth en) {
    Double minDistance = Double.POSITIVE_INFINITY;
    for (Node node : way.getNodes()) {
      Double distance = node.getEastNorth().distance(en);
      if (distance < minDistance) {
        minDistance = distance;
      }
    }
    return minDistance;
  }
  
  
}
