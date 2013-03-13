package org.openstreetmap.josm.plugins.openservices;

import org.apache.commons.configuration.ConfigurationException;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

public interface OdsDataSource {
  public String getFeatureType();
  public FeatureMapper getFeatureMapper();
  public OdsDownloadTask getDownloadTask();
  public void addFeatures(FeatureCollection<?, SimpleFeature> features);
  public void setService(Service service);
  public void addFeatureListener(FeatureListener featureListener);
  public void setFilter(Filter filter) throws ConfigurationException;
  public Filter getFilter();
}
