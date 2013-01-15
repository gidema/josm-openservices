package org.openstreetmap.josm.plugins.openservices;

import org.openstreetmap.josm.gui.layer.OsmDataLayer;

/**
 * Custom data layer.
 * Provides storing downloaded data in a format specific for the data,
 * thus allowing for better data merging capabilities.
 *  
 * @author Gertjan Idema
 *
 */
public class CustomDataLayer extends OsmDataLayer {

  public CustomDataLayer(CustomDataSet dataSet, String name) {
    super(dataSet, name, null);
  }
}
