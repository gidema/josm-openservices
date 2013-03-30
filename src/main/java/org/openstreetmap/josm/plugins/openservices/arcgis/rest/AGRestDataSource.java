package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import org.openstreetmap.josm.plugins.openservices.AbstractOdsDataSource;
import org.openstreetmap.josm.plugins.openservices.OdsDownloadTask;

public class AGRestDataSource extends AbstractOdsDataSource {

  @Override
  public OdsDownloadTask getDownloadTask() {
    return new AGRestDownloadTask(this);
  }
}
