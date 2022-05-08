package org.openstreetmap.josm.plugins.ods.entities;

import org.openstreetmap.josm.plugins.ods.context.OdsContextJob;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;

/**
 * Builder to create OSM primitives for open data Entities.
 * The resulting OsmPrimitive is added to the entity.
 *
 * @author Gertjan Idema
 *
 * @param <T>
 */
public interface EntityPrimitiveBuilder<T extends OdEntity> extends OdsContextJob {

    void createPrimitive(T entity, OdLayerManager layerManager);
}
