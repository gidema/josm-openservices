package org.openstreetmap.josm.plugins.openservices.metadata;

public interface MetaDataLoader {
  void populateMetaData(MetaData metaData) throws MetaDataException;
}
