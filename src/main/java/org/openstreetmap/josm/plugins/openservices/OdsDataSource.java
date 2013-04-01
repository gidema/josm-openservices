package org.openstreetmap.josm.plugins.openservices;

import java.util.Collection;

import org.apache.commons.configuration.ConfigurationException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

public interface OdsDataSource {
  public String getFeatureType();
  public FeatureMapper getFeatureMapper();
  public OdsDownloadTask getDownloadTask(Collection<SimpleFeature> featureCollection);
  public void setService(Service service);
  public Service getService();
  public void setFilter(Filter filter) throws ConfigurationException;
  public Filter getFilter();
  public void setIdFactory(DefaultIdFactory idFactory);
  public IdFactory getIdFactory();
}
