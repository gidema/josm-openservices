package org.openstreetmap.josm.plugins.openservices.metadata;

import nl.gertjanidema.conversion.valuemapper.ValueMapper;

public class MetaDataAttribute {
  private final String name;
  private final String query;
  private final ValueMapper<?> valueMapper;

  
  public MetaDataAttribute(String name, String query, ValueMapper<?> valueMapper) {
    super();
    this.name = name;
    this.query = query;
    this.valueMapper = valueMapper;
  }

  public final String getName() {
    return name;
  }

  public final String getQuery() {
    return query;
  }

  public final ValueMapper<?> getValueMapper() {
    return valueMapper;
  }
}
