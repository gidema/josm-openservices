package org.openstreetmap.josm.plugins.ods.entities;

import java.util.Collection;
import java.util.Map;

/**
 * An OsmEntity is an Entity that has been created from one or more OSM primitives.
 * There doesn't necessarily have to a 1 to 1 relation between an OdEntity and an OSM
 * primitive. OSM primitives can be combined or split to create 1 or more
 * OsmEntities.
 *
 * @author Gertjan Idema
 *
 */
public interface OsmEntity extends Entity {
    /**
     * Get the tags that are not associated with any of the entity's properties.
     */
    public Map<String, String> getOtherTags();

    public void addDeviation(Deviation deviation);

    public Collection<Deviation> getDeviations();

    public <T extends Deviation> T getDeviation(Class<T> type);

    public void removeDeviation(Class<?> type);
}
