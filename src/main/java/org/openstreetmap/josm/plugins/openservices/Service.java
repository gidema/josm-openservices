package org.openstreetmap.josm.plugins.openservices;

import java.util.concurrent.FutureTask;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.data.Bounds;

public interface Service {
  public void setHost(Host host);
  public void setFeature(String feature) throws ServiceException;
  public void init() throws ServiceException;
  public CoordinateReferenceSystem getCrs();
  public String getSRS();
  public Long getSRID();
  public FutureTask<FeatureCollection<?, ?>> getDownloadTask(Bounds bounds);
  public FeatureType getFeatureType();
}
