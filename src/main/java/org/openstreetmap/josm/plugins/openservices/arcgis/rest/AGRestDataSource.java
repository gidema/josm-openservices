package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import java.util.Set;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.openservices.AbstractOdsDataSource;
import org.openstreetmap.josm.plugins.openservices.DownloadJob;
import org.openstreetmap.josm.plugins.openservices.ImportDataLayer;
import org.openstreetmap.josm.plugins.openservices.OdsFeatureSource;
import org.openstreetmap.josm.plugins.openservices.entities.Entity;

public class AGRestDataSource extends AbstractOdsDataSource {

  protected AGRestDataSource(OdsFeatureSource odsFeatureSource) {
    super(odsFeatureSource);
  }

  @Override
  public DownloadJob createDownloadJob(ImportDataLayer dataLayer, Bounds bounds, Set<Entity> newEntities) {
    return new AGRestDownloadJob(this, bounds);
  }
}
