package org.openstreetmap.josm.plugins.ods.entities.osm;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

/**
 * Implementations of this interface can create OSM entities from OsmPrimitives.
 *
 * @author Gertjan Idema
 *
 * @param <T> The class of the Entity that the implementation returns.
 */
public interface OsmEntityBuilder<T extends Entity> {


    /**
     * Check if this builder can handle the provided primitive and create an Entity of
     * type T
     * @param primitive The primitive to check
     * @return true if the primitive can be handled. False otherwise.
     */
    public boolean canHandle(OsmPrimitive primitive);

    /**
     * Create an Entity of type T from the given primitive.
     *
     * If the given primitive is recognized, create an entity for the primitive ands the
     * @param primitive
     */
    public void buildOsmEntity(OsmPrimitive primitive);

    /**
     * Get the Entity class that this builder produces.
     * @return The class of T
     */
    public Class<T> getEntityClass();
}
