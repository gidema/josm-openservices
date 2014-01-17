package org.openstreetmap.josm.plugins.ods.geotools;

import org.openstreetmap.josm.plugins.ods.AbstractOdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDownloadTask;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;

import com.vividsolutions.jts.geom.LinearRing;

public class GtDataSource extends AbstractOdsDataSource {
  
  protected GtDataSource(OdsFeatureSource odsFeatureSource) {
    super(odsFeatureSource);
  }

  @Override
  public ExternalDownloadTask createDownloadTask(Boundary boundary) {
    return new GtDownloadTask(this, boundary);
  }
}
