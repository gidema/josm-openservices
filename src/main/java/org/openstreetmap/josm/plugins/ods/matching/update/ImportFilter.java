package org.openstreetmap.josm.plugins.ods.matching.update;

import java.util.function.Predicate;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;

public interface ImportFilter extends Predicate<OdEntity> {
    // Intentionally empty
}
