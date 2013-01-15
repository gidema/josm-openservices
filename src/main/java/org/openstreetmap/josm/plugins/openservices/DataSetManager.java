package org.openstreetmap.josm.plugins.openservices;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;


public interface DataSetManager {
  public DataSet getDataSet();
  public OsmDataLayer getDataLayer();
}
