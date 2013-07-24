package org.openstreetmap.josm.plugins.openservices.geotools;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.openservices.AbstractOdsDataSource;
import org.openstreetmap.josm.plugins.openservices.DownloadJob;
import org.openstreetmap.josm.plugins.openservices.OdsFeatureSource;

public class GtDataSource extends AbstractOdsDataSource {
  
  protected GtDataSource(OdsFeatureSource odsFeatureSource) {
    super(odsFeatureSource);
  }

  @Override
  public DownloadJob createDownloadJob(Bounds bounds) {
    return new GtDownloadJob(this, bounds);
  }
}
