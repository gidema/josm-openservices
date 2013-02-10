package org.openstreetmap.josm.plugins.openservices;

import org.opengis.feature.Feature;

public interface FeatureMapper {
  public String getFeatureName();
  public void mapFeature(Feature feature, JosmObjectFactory objectFactory);
}
