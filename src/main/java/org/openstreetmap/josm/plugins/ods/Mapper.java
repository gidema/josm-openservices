package org.openstreetmap.josm.plugins.ods;

/**
 * Find a relation between down-loaded Open Data (OD) entities and OSM entities.
 * Implementations run over all OD entities to see if there is a matching OSM entity.
 * Matches get registered in a Mapping object and are then bound to the entities on both sides.
 *
 * @author Gertjan Idema
 *
 */
public interface Mapper {

    /**
     * Run this matcher
     */
    void run();

    /**
     * Reset this matcher, releasing any state.
     */
    void reset();
}
