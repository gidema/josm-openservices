package org.openstreetmap.josm.plugin.openservices.wfs;


import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugin.openservices.CustomDataSet;

public abstract class WFSDataSet<T> extends CustomDataSet<T> {
  private final WFSFeatureParser<T> featureParser;

  public WFSDataSet(WFSFeatureParser<T> featureParser) {
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
    T object = featureParser.parse(feature);
    // TODO add possibility to filter non-existing objects (INGETROKKEN etc)
    add(object);
  }
}
