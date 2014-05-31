package org.openstreetmap.josm.plugins.ods.metadata;

import java.util.HashMap;
import java.util.Map;

public class MetaData {
  private final MetaData parent;
  private final Map<String, Object> metaData = new HashMap<>();
  
  public MetaData() {
    this(null);
  }
  
  public MetaData(MetaData parent) {
    this.parent = parent;
  }
  
  public void put(String key, Object value) {
    metaData.put(key, value);
  }

  public Object get(Object key) {
    Object value = metaData.get(key);
    if (value == null && parent != null) {
      value = parent.get(key);
    }
    return value;
  }

  public boolean containsKey(Object key) {
    if (metaData.containsKey(key)) {
      return true;
    }
    if (parent != null) {
        return parent.containsKey(key);
    }
    return false;
  }
}
