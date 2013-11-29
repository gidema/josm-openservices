package org.openstreetmap.josm.plugins.ods.metadata;

public interface MetaDataLoader {
  void populateMetaData(MetaData metaData) throws MetaDataException;
}
