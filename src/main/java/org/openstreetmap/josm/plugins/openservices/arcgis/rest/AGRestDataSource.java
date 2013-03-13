package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import org.openstreetmap.josm.plugins.openservices.AbstractOdsDataSource;
import org.openstreetmap.josm.plugins.openservices.ODSDownloadTask;

public class AGRestDataSource extends AbstractOdsDataSource {

  @Override
  public ODSDownloadTask getDownloadTask() {
    return new AGRestDownloadTask(service, this);
  }
}
