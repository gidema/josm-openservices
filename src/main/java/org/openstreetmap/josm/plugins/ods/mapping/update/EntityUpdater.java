package org.openstreetmap.josm.plugins.ods.mapping.update;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.mapping.Mapping;

/**
 *
 * @author Gertjan Idema
 */
public interface EntityUpdater {
    void update(List<Mapping<?, ?>> matches);
}
