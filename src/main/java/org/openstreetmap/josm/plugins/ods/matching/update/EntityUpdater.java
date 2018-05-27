package org.openstreetmap.josm.plugins.ods.matching.update;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.matching.Match;

/**
 *
 * @author Gertjan Idema
 */
public interface EntityUpdater {
    void update(List<Match<?, ?>> matches);
}
