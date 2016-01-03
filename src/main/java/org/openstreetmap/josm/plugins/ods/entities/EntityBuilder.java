package org.openstreetmap.josm.plugins.ods.entities;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public interface EntityBuilder<T, U extends Entity> {
    public U build(SimpleFeature feature, DownloadResponse response);
}
