package org.openstreetmap.josm.plugins.openservices;

import org.apache.commons.configuration.ConfigurationException;
import org.opengis.filter.Filter;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaData;
import org.openstreetmap.josm.plugins.openservices.tags.FeatureMapper;

/**
 * An OdsDataSource is the interface between the OdsWorkingSet and the
 * OdsFeatureSource. It performs the following tasks.
 * 
 * - Maintain a filter used when downloading features
 * - Create a unique id for each downloaded feature
 * - Maintain a list of downloaded feature to prevent duplicates
 * 
 * @author Gertjan Idema
 *
 */
public interface OdsDataSource {
  public String getFeatureType();
  public FeatureMapper getFeatureMapper();
  public OdsFeatureSource getOdsFeatureSource();
  public void setFilter(Filter filter) throws ConfigurationException;
  public Filter getFilter();
  public void setIdFactory(DefaultIdFactory idFactory);
  public IdFactory getIdFactory();
  public MetaData getMetaData();
  DownloadJob createDownloadJob(Bounds bounds);
  void addFeatureListener(FeatureListener featureListener);
}
