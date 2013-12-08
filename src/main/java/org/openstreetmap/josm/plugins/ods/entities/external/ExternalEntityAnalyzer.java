package org.openstreetmap.josm.plugins.ods.entities.external;

import java.util.Collection;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;

public interface ExternalEntityAnalyzer {

    void setEntitySet(EntitySet entitySet);

    void analyzeNewEntities(Collection<Entity> entities, Bounds bounds);
}
