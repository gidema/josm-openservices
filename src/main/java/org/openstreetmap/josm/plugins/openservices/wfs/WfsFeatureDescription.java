package org.openstreetmap.josm.plugins.openservices.wfs;

import org.openstreetmap.josm.plugins.openservices.FeatureDescription;
import org.openstreetmap.josm.plugins.openservices.Service;


public class WfsFeatureDescription implements FeatureDescription {
  private final Service service;
  private final WFSHost wFSHost;
  private final String featureName;

  protected WfsFeatureDescription(Service service, WFSHost wFSHost, String featureName) {
    super();
    this.service = service;
    this.wFSHost = wFSHost;
    this.featureName = featureName;
  }

  public Service getService() {
    return service;
  }
  
  public WFSHost getWFSHost() {
    return wFSHost;
  }

  public String getFeatureName() {
    return featureName;
  }
}
