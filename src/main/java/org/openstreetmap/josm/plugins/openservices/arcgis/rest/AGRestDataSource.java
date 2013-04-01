package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import java.util.Collection;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.openservices.AbstractOdsDataSource;
import org.openstreetmap.josm.plugins.openservices.OdsDownloadTask;

public class AGRestDataSource extends AbstractOdsDataSource {

  @Override
  public OdsDownloadTask getDownloadTask(Collection<SimpleFeature> featureCollection) {
    return new AGRestDownloadTask(this, featureCollection);
  }
}
