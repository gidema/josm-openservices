package org.openstreetmap.josm.plugins.openservices;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;

public class DefaultDataSetManager implements DataSetManager {
  private final DataSet dataSet = new DataSet();
  private final String layerName;
  private OsmDataLayer dataLayer = null;
  
  public DefaultDataSetManager(String layerName) {
    super();
    this.layerName = layerName;
  }

  @Override
  public DataSet getDataSet() {
    return dataSet;
  }

  @Override
  public OsmDataLayer getDataLayer() {
    if (dataLayer == null) {
      dataLayer = new OsmDataLayer(dataSet, layerName, null);
      Main.main.addLayer(dataLayer);
    }
    return dataLayer;
  }
}
