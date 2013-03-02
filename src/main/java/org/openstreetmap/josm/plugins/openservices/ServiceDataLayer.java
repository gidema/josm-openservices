package org.openstreetmap.josm.plugins.openservices;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;

public class ServiceDataLayer extends OsmDataLayer {

  public ServiceDataLayer(String name) {
    super(new DataSet(), name, null);
  }

}
