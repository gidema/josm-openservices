package org.openstreetmap.josm.plugins.openservices.tags;

import java.util.Map;

import org.opengis.feature.Feature;

public class FixedTagBuilder implements TagBuilder {
  private final String key;
  private final String value;

  public FixedTagBuilder(String key, String value) {
    super();
    this.key = key;
    this.value = value;
  }

  @Override
  public void createTag(Map<String, String> tags, Feature feature) {
    tags.put(key, value);
  }
}
