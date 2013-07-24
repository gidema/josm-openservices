package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.openservices.AbstractOdsDataSource;
import org.openstreetmap.josm.plugins.openservices.DownloadJob;
import org.openstreetmap.josm.plugins.openservices.OdsFeatureSource;

public class AGRestDataSource extends AbstractOdsDataSource {

  protected AGRestDataSource(OdsFeatureSource odsFeatureSource) {
    super(odsFeatureSource);
  }

  @Override
  public DownloadJob createDownloadJob(Bounds bounds) {
    return new AGRestDownloadJob(this, bounds);
  }
}
