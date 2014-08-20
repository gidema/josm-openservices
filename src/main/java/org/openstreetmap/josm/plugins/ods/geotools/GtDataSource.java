package org.openstreetmap.josm.plugins.ods.geotools;

import org.openstreetmap.josm.plugins.ods.DefaultOdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDownloadTask;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;

public class GtDataSource extends DefaultOdsDataSource {
  
  public GtDataSource(OdsFeatureSource odsFeatureSource) {
    super(odsFeatureSource);
  }

//  @Override
//  public ExternalDownloadTask createDownloadTask(Boundary boundary) {
//    return new GtDownloadTask(this, boundary);
//  }
}
