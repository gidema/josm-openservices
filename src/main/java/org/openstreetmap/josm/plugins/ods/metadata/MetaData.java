package org.openstreetmap.josm.plugins.ods.metadata;

import java.util.HashMap;
import java.util.Map;

public class MetaData {
  private final MetaData parent;
  private final Map<String, Object> metaData = new HashMap<String, Object>();
  private final boolean initialized = false;
  
  public MetaData() {
    this(null);
  }
  
  public MetaData(MetaData parent) {
    this.parent = parent;
  }
  
  protected void put(String key, Object value) {
    metaData.put(key, value);
  }

  public Object get(Object key) throws MetaDataException {
    Object value = metaData.get(key);
    if (value == null && parent != null) {
      value = parent.get(key);
    }
    return value;
  }

  public boolean containsKey(Object key) throws MetaDataException {
    if (metaData.containsKey(key)) {
      return true;
    }
    return parent.containsKey(key);
  }
}
