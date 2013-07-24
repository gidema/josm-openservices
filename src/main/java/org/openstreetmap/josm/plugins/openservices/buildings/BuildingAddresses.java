package org.openstreetmap.josm.plugins.openservices.buildings;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.openservices.OdsWorkingSet;

public class BuildingAddresses {
  private OdsWorkingSet workingSet;
  
  private final Collection<Way> buildings = new LinkedList<Way>();
  private final Collection<Node> adresses = new LinkedList<Node>();
  private final Set<String> streetNames = new HashSet<String>();

  private void getOdsBuildings() {
    for (Way way:workingSet.getOdsDataLayer().data.getWays()) {
      if (way.hasKey("building")) {
        buildings.add(way);
      }
    }
    for (Node node:workingSet.getOdsDataLayer().data.getNodes()) {
      if (node.hasKey("addr:housenumber")) {
        
      }
    }
  }
}
