package org.openstreetmap.josm.plugins.openservices.metadata;

public interface Contextualizable {
  public void setContext(MetaData metaData);
  public void addMetaDataLoader(MetaDataLoader metaDataLoader);
  public MetaData getContext();
}
