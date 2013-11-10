package org.openstreetmap.josm.plugins.openservices.geotools;

import java.util.Set;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.openservices.AbstractOdsDataSource;
import org.openstreetmap.josm.plugins.openservices.DownloadJob;
import org.openstreetmap.josm.plugins.openservices.ImportDataLayer;
import org.openstreetmap.josm.plugins.openservices.OdsFeatureSource;
import org.openstreetmap.josm.plugins.openservices.entities.Entity;

public class GtDataSource extends AbstractOdsDataSource {
  
  protected GtDataSource(OdsFeatureSource odsFeatureSource) {
    super(odsFeatureSource);
  }

  @Override
  public DownloadJob createDownloadJob(ImportDataLayer dataLayer, Bounds bounds, Set<Entity> newEntities) {
    return new GtDownloadJob(this, dataLayer, bounds, newEntities);
  }
}
