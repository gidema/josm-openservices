package org.openstreetmap.josm.plugins.openservices.wfs;

public class WfsFeature {
  private final WfsHost wfsHost;
  private final String featureName;

  protected WfsFeature(WfsHost wfsHost, String featureName) {
    super();
    this.wfsHost = wfsHost;
    this.featureName = featureName;
  }

  public WfsHost getWfsHost() {
    return wfsHost;
  }

  public String getFeatureName() {
    return featureName;
  }
}
