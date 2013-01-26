package org.openstreetmap.josm.plugins.openservices.wfs;

import org.opengis.feature.simple.SimpleFeature;

public interface WFSFeatureParser {
  public Object parse(SimpleFeature feature);
}
