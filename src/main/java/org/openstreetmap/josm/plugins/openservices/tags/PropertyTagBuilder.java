package org.openstreetmap.josm.plugins.openservices.tags;

import java.util.Map;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;

public class PropertyTagBuilder implements TagBuilder {
  private final String key;
  private final String property;
  private final String format;
  
  public PropertyTagBuilder(String key, String property, String format) {
    super();
    this.key = key;
    this.property = property;
    this.format = format;
  }

  @Override
  public void createTag(Map<String, String> tags, Feature feature) {
    Property p = feature.getProperty(property);
    if (p == null) return;
    Object o = p.getValue();
    if (o == null) return;
    String value = null;
    if (format != null) {
      value = String.format(format, o); 
    }
    else {
      value = o.toString();
    }
    tags.put(key, value);
  }
}
