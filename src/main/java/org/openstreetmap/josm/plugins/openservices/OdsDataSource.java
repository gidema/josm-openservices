package org.openstreetmap.josm.plugins.openservices;

import org.apache.commons.configuration.ConfigurationException;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.openstreetmap.josm.data.Bounds;

public interface OdsDataSource {
  public String getFeatureType();
  public FeatureMapper getFeatureMapper();
  public OdsDownloadTask getDownloadTask();
  public void addFeatures(FeatureCollection<?, SimpleFeature> features, Bounds area);
  public void setService(Service service);
  public void addFeatureListener(FeatureListener featureListener);
  public void setFilter(Filter filter) throws ConfigurationException;
  public Filter getFilter();
  public void setIdFactory(DefaultIdFactory idFactory);
}
