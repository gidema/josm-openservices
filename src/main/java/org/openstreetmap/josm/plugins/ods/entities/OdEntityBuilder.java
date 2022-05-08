package org.openstreetmap.josm.plugins.ods.entities;

import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;

/**
 * An OdEntityBuilder creates OdEntities from open data features.
 *
 * @author Gertjan Idema
 *
 * @param <U>
 */
public interface OdEntityBuilder<T extends OdEntity> {
    public T build(WfsFeature feature, DownloadResponse response);
}
