package org.openstreetmap.josm.plugins.ods;

import java.io.Serializable;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public interface IdFactory {
  public SimpleFeatureType getFeatureType();
  public Serializable getId(SimpleFeature feature);
}
