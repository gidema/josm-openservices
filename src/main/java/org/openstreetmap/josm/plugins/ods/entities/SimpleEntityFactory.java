package org.openstreetmap.josm.plugins.ods.entities;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalEntity;
import org.openstreetmap.josm.plugins.ods.entities.external.SimpleExternalEntity;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class SimpleEntityFactory implements EntityFactory {

    @Override
    public ExternalEntity buildEntity(String type, MetaData metaData, SimpleFeature feature) throws BuildException {
        ExternalEntity entity = new SimpleExternalEntity(feature);
        entity.init(metaData);
        return entity;
    }
}
