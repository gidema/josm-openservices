package org.openstreetmap.josm.plugins.openservices;

import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public interface Service {
  public void setHost(Host host);
  public void setFeatureName(String feature) throws ServiceException;
  public String getFeatureName();
  public void init() throws ServiceException;
  public CoordinateReferenceSystem getCrs();
  public String getSRS();
  public Long getSRID();
  public FeatureType getFeatureType();
  public OdsDataSource newDataSource();
}
