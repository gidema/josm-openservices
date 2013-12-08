package org.openstreetmap.josm.plugins.ods.entities;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalEntityAnalyzer;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public interface EntityFactory {
    public Entity buildEntity(String entityType, MetaData metaData, SimpleFeature feature) throws BuildException;

    public ExternalEntityAnalyzer getEntityAnalyzer();
}
