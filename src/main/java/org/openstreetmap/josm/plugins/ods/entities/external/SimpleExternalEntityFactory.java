package org.openstreetmap.josm.plugins.ods.entities.external;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

// TODO This class is broken
public class SimpleExternalEntityFactory implements EntityFactory<SimpleFeature> {

    @Override
    public ExternalEntity buildEntity(SimpleFeature feature, MetaData metaData) throws BuildException {
        ExternalEntity entity = new SimpleExternalEntity(feature, null);
        entity.init(metaData);
        return entity;
    }
}
