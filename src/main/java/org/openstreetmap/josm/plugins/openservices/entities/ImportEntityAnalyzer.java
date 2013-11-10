package org.openstreetmap.josm.plugins.openservices.entities;

import java.util.Collection;

import org.openstreetmap.josm.data.Bounds;

public interface ImportEntityAnalyzer {

    void setEntitySet(EntitySet entitySet);

    void analyzeNewEntities(Collection<Entity> entities, Bounds bounds);
}
