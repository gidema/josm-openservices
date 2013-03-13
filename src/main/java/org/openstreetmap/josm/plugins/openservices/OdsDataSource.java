package org.openstreetmap.josm.plugins.openservices;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

public interface OdsDataSource {
  public String getFeatureType();
  public FeatureMapper getFeatureMapper();
  public ODSDownloadTask getDownloadTask();
  public void addFeatures(FeatureCollection<?, SimpleFeature> features);
  public void setService(Service service);
  public void addFeatureListener(FeatureListener featureListener);
}
