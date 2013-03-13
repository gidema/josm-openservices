package org.openstreetmap.josm.plugins.openservices.geotools;

import org.openstreetmap.josm.plugins.openservices.AbstractOdsDataSource;
import org.openstreetmap.josm.plugins.openservices.OdsDownloadTask;

public class GtDataSource extends AbstractOdsDataSource {
  
  @Override
  public OdsDownloadTask getDownloadTask() {
    // TODO Can we cache this object instead of recreating it?
    return new GtDownloadTask((GtService) service, this);
  }
}
