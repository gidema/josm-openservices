package org.openstreetmap.josm.plugins.openservices.wfs;


import java.io.Serializable;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.openservices.CustomDataSet;

public class WFSDataSet extends CustomDataSet {
  private WFSFeatureParser featureParser;

  public WFSDataSet() {
    super();
  }
  
  public void setFeatureParser(WFSFeatureParser featureParser) {
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
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      i.close();
    }
  }
  
  public void addFeature(SimpleFeature feature) {
    if (featureParser != null) {
      add(featureParser.parse(feature));
    }
    else {
      add(feature);
    }
  }

  @Override
  protected Serializable getId(Object o) {
    if (o instanceof SimpleFeature) {
      return getId((SimpleFeature) o);
    }
    return null;
  }

  protected Serializable getId(SimpleFeature feature) {
    return feature.getID();
  }
}
