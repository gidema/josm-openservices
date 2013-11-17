package org.openstreetmap.josm.plugins.openservices.entities.imported;

import java.util.Collection;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.openservices.entities.Entity;
import org.openstreetmap.josm.plugins.openservices.entities.EntitySet;

public interface ImportedEntityAnalyzer {

    void setEntitySet(EntitySet entitySet);

    void analyzeNewEntities(Collection<Entity> entities, Bounds bounds);
}
