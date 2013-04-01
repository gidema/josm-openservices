package org.openstreetmap.josm.plugins.openservices.geotools;

import java.util.Collection;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.openservices.AbstractOdsDataSource;
import org.openstreetmap.josm.plugins.openservices.OdsDownloadTask;

public class GtDataSource extends AbstractOdsDataSource {
  
  @Override
  public OdsDownloadTask getDownloadTask(Collection<SimpleFeature> featureCollection) {
    // TODO Can we cache this object instead of recreating it?
    return new GtDownloadTask(this, featureCollection);
  }
}
