package org.openstreetmap.josm.plugins.openservices.wfs;


import java.io.Serializable;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.openservices.CustomDataSet;

public abstract class WFSDataSet extends CustomDataSet {
  private final WFSFeatureParser featureParser;

  public WFSDataSet(WFSFeatureParser featureParser) {
    super();
    this.featureParser = featureParser;
  }

  /**
   * Add WFS features to the dataSet
   * @param features
   */
  public void addFeatures(SimpleFeatureCollection features) {
    SimpleFeatureIterator i = features.features();
    try {
      while (i.hasNext()) {
        SimpleFeature feature = i.next();
        addFeature(feature);
      }
    }
    finally {
      i.close();
    }
  }
  
  public void addFeature(SimpleFeature feature) {
    Object object = featureParser.parse(feature);
    // TODO add possibility to filter non-existing objects (INGETROKKEN etc)
    add(object);
  }

  protected Serializable getId(SimpleFeature feature) {
    return feature.getID();
  }
}
