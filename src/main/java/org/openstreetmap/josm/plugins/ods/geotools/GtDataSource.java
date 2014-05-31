package org.openstreetmap.josm.plugins.ods.geotools;

import org.openstreetmap.josm.plugins.ods.AbstractOdsDataSource;
import org.openstreetmap.josm.plugins.ods.DownloadTask;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;

public class GtDataSource extends AbstractOdsDataSource {
  
  protected GtDataSource(OdsFeatureSource odsFeatureSource) {
    super(odsFeatureSource);
  }

  @Override
  public DownloadTask createDownloadTask(Boundary boundary) {
    return new GtDownloadTask(this, boundary);
  }
}
