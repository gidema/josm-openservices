package org.openstreetmap.josm.plugins.openservices;

import java.util.List;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.Bounds;

public interface FeatureListener {
  public void featureAdded(Feature feature);

  public void featuresAdded(List<SimpleFeature> newFeatures, Bounds bounds);
}
