package org.openstreetmap.josm.plugins.ods.entities;

/**
 * Builder to create OSM primitives for open data Entities.
 * The resulting OsmPrimitive is added to the entity.
 *
 * @author Gertjan Idema
 *
 * @param <T>
 */
public interface EntityPrimitiveBuilder<T extends OdEntity> extends Runnable {

    void createPrimitive(T entity);
}
