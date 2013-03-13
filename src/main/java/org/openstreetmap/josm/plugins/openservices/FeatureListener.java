package org.openstreetmap.josm.plugins.openservices;

import java.util.List;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;

public interface FeatureListener {
  public void featureAdded(Feature feature);

  public void featuresAdded(List<SimpleFeature> newFeatures);
}
