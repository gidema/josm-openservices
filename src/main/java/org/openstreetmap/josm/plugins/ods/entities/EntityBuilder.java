package org.openstreetmap.josm.plugins.ods.entities;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public interface EntityBuilder<T, U extends Entity> {
    public U build(SimpleFeature feature, MetaData metaData,
            DownloadResponse response);
}
