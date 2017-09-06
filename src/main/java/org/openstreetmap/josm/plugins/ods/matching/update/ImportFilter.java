package org.openstreetmap.josm.plugins.ods.matching.update;

import java.util.function.Predicate;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface ImportFilter extends Predicate<Entity> {
    // Intentionally empty
}
