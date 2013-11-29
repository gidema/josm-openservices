package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.Set;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.ods.AbstractOdsDataSource;
import org.openstreetmap.josm.plugins.ods.DownloadJob;
import org.openstreetmap.josm.plugins.ods.ImportDataLayer;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public class GtDataSource extends AbstractOdsDataSource {
  
  protected GtDataSource(OdsFeatureSource odsFeatureSource) {
    super(odsFeatureSource);
  }

  @Override
  public DownloadJob createDownloadJob(ImportDataLayer dataLayer, Bounds bounds, Set<Entity> newEntities) {
    return new GtDownloadJob(this, dataLayer, bounds, newEntities);
  }
}
