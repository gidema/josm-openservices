package org.openstreetmap.josm.plugins.openservices;

import java.util.Map;

import org.opengis.feature.Feature;

public interface TagBuilder {
  void createTag(Map<String, String> tags, Feature feature);
}
