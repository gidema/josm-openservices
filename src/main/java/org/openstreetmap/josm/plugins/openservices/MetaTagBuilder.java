package org.openstreetmap.josm.plugins.openservices;

import java.util.Map;

import org.opengis.feature.Feature;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaData;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaDataException;

public class MetaTagBuilder implements TagBuilder {
  private final String key;
  private final String property;
  private final String format;
  private String value;
  
  public MetaTagBuilder(String key, String property, String format) {
    super();
    this.key = key;
    this.property = property;
    this.format = format;
  }
  
  public void setContext(MetaData context) throws MetaDataException {
    Object o = context.get(property);
    if (o == null) return;
    if (format != null) {
      value = String.format(format, o); 
    }
    else {
      value = o.toString();
    }
  }

  @Override
  public void createTag(Map<String, String> tags, Feature feature) {
    tags.put(key, value);
  }

}
