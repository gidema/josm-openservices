package org.openstreetmap.josm.plugins.ods.entities;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

/**
 * An OdEntityBuilder creates OdEntities from open data features.
 *
 * @author Gertjan Idema
 *
 * @param <U>
 */
public interface OdEntityBuilder<T extends OdEntity> {
    public T build(SimpleFeature feature, DownloadResponse response);
}
