package org.openstreetmap.josm.plugins.ods.geotools;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.ods.AbstractOdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDownloadTask;

public class GtDataSource extends AbstractOdsDataSource {
  
  protected GtDataSource(OdsFeatureSource odsFeatureSource) {
    super(odsFeatureSource);
  }

  @Override
  public ExternalDownloadTask createDownloadTask(Bounds bounds) {
    return new GtDownloadTask(this, bounds);
  }
}
