package org.openstreetmap.josm.plugin.openservices;

import org.openstreetmap.josm.data.osm.DataSet;

public abstract class CustomDataSet<T> extends DataSet {
  public abstract void add(T o);
}
