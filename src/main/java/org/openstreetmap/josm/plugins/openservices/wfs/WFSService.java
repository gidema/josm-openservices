package org.openstreetmap.josm.plugins.openservices.wfs;

import java.io.IOException;

import org.geotools.data.FeatureSource;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.openservices.Host;
import org.openstreetmap.josm.plugins.openservices.Service;
import org.openstreetmap.josm.plugins.openservices.ServiceException;

public class WFSService implements Service {
  final static String type = "WFS";
  private boolean initialized = false;
  WFSHost host;
  String feature;
  FeatureSource<?, ?> featureSource;
  CoordinateReferenceSystem crs;


  @Override
  public void setHost(Host host) {
    this.host = (WFSHost) host;
  }
  
  @Override
  public void setFeature(String feature) {
    this.feature = host.getName() + ":" + feature;
  }

  @Override
  public void init() throws ServiceException {
    if (initialized) return;
    initialize();
    initialized = true;
  }
  
  private void initialize() throws ServiceException {
    if (!host.hasFeatureType(feature)) {
      throw new WfsException(String.format("Unknown feature type: '%s'", feature));
    }
    try {
      featureSource = host.getDataStore().getFeatureSource(feature);
    } catch (IOException e) {
      throw new ServiceException(e);
    }
  }
  
  public FeatureSource<?, ?> getFeatureSource() {
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
}
