package org.openstreetmap.josm.plugins.openservices.wfs;

import org.opengis.feature.simple.SimpleFeature;

public interface WFSFeatureParser<T> {
  public T parse(SimpleFeature feature);
}
