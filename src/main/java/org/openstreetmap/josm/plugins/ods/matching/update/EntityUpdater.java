package org.openstreetmap.josm.plugins.ods.matching.update;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.matching.Match;

/**
 * TODO The generics for implementations of this interface are a mess.
 * Find a neat solution for this.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 * @param <T>
 */
public interface EntityUpdater<T extends Entity> {
    void update(Match<? extends Entity> match);
}
