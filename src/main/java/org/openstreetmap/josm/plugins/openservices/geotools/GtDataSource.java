package org.openstreetmap.josm.plugins.openservices.geotools;

import org.openstreetmap.josm.plugins.openservices.AbstractOdsDataSource;
import org.openstreetmap.josm.plugins.openservices.ODSDownloadTask;

public class GtDataSource extends AbstractOdsDataSource {
  
  @Override
  public ODSDownloadTask getDownloadTask() {
    // TODO Can we cache this object instead of recreating it?
    return new GtDownloadTask((GtService) service, this);
  }
}
