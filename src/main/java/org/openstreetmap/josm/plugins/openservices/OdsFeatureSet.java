package org.openstreetmap.josm.plugins.openservices;

import java.util.Collection;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaData;

public class OdsFeatureSet {
  // private Bounds boundingBox;
  private final FeatureType featureType;
  private final Collection<SimpleFeature> features;
  private final MetaData metaData;

  
  public OdsFeatureSet(FeatureType featureType,
      Collection<SimpleFeature> features, MetaData metaData) {
    super();
    this.featureType = featureType;
    this.features = features;
    this.metaData = metaData;
  }

  // public final Bounds getBoundingBox() {
  // return boundingBox;
  // }
  // public final void setBoundingBox(Bounds boundingBox) {
  // this.boundingBox = boundingBox;
  // }
  public final FeatureType getFeatureType() {
    return featureType;
  }

  public final Collection<SimpleFeature> getFeatures() {
    return features;
  }

  public final MetaData getMetaData() {
    return metaData;
  }
}
