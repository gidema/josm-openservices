package org.openstreetmap.josm.plugins.ods.matching;

import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

public interface AspectAnalyzer<T extends OsmEntity> {
    public void analyze(T entity);
}
