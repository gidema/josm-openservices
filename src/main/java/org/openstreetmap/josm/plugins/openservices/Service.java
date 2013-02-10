package org.openstreetmap.josm.plugins.openservices;

import java.util.concurrent.FutureTask;

import org.geotools.feature.FeatureCollection;
import org.openstreetmap.josm.data.Bounds;

public interface Service {
  public void setHost(Host host);
  public void setFeature(String feature) throws ServiceException;
  public FutureTask<FeatureCollection<?, ?>> getDownloadTask(Bounds bounds);
  public String getSRS();
  public int getSRID();
}
