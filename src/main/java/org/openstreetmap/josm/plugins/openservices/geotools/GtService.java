package org.openstreetmap.josm.plugins.openservices.geotools;

import java.io.IOException;

import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.openservices.Host;
import org.openstreetmap.josm.plugins.openservices.OdsDataSource;
import org.openstreetmap.josm.plugins.openservices.Service;
import org.openstreetmap.josm.plugins.openservices.ServiceException;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaData;

public class GtService implements Service {
  private boolean initialized = false;
  GtHost host;
  String featureName;
  FeatureSource<?, SimpleFeature> featureSource;
  CoordinateReferenceSystem crs;
  MetaData metaData;

  @Override
  public void setHost(Host host) {
    this.host = (GtHost)host;
  }
  
  @Override
  public void setFeatureName(String feature) {
    this.featureName = host.getName() + ":" + feature;
  }

  @Override
  public final String getFeatureName() {
    return featureName;
  }

  @Override
  public void init() throws ServiceException {
    if (initialized) return;
    initialize();
    initialized = true;
  }
  
  private void initialize() throws ServiceException {
    metaData = new MetaData(host.getMetaData());
    if (!host.hasFeatureType(featureName)) {
      throw new GtException(String.format("Unknown featureName type: '%s'", featureName));
    }
    try {
      featureSource = host.getDataStore().getFeatureSource(featureName);
    } catch (IOException e) {
      throw new ServiceException(e);
    }
  }
  
  @Override
  public MetaData getMetaData() {
    return metaData;
  }

  public FeatureSource<?, SimpleFeature> getFeatureSource() {
    return featureSource;
  }
  
  @Override
  public FeatureType getFeatureType() {
    return getFeatureSource().getSchema();
  }

  @Override
  public CoordinateReferenceSystem getCrs() {
    if (crs == null) {
      crs = featureSource.getInfo().getCRS();
    }
    return crs;
  }
  
  @Override
  public String getSRS() {
    ReferenceIdentifier rid = crs.getIdentifiers().iterator().next();
    return rid.toString();
  }
  
  @Override
  public Long getSRID() {
    ReferenceIdentifier rid = crs.getIdentifiers().iterator().next();
    return Long.parseLong(rid.getCode());
  }

  @Override
  public OdsDataSource newDataSource() {
    return new GtDataSource();
  }
}
