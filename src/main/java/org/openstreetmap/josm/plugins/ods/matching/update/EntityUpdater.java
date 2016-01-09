package org.openstreetmap.josm.plugins.ods.matching.update;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.matching.Match;

/**
 * TODO The generics for implementations of this interface are a mess.
 * Find a neat solution for this.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 * @param <T>
 */
public interface EntityUpdater {
    void update(List<Match<?>> matches);
}
