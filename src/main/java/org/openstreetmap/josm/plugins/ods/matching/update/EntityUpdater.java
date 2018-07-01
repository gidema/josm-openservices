package org.openstreetmap.josm.plugins.ods.matching.update;

import java.util.Collection;

import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

/**
 *
 * @author Gertjan Idema
 */
public interface EntityUpdater<T extends OsmEntity> {
    public void update(Collection<T> entities);
}
