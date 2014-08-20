package org.openstreetmap.josm.plugins.ods.entities.external;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class SimpleExternalEntityFactory implements EntityFactory {

    @Override
    public ExternalEntity buildEntity(MetaData metaData, SimpleFeature feature) throws BuildException {
        ExternalEntity entity = new SimpleExternalEntity(feature);
        entity.init(metaData);
        return entity;
    }

    @Override
    public Entity buildEntity(String entityType, OsmPrimitive primitive)
            throws BuildException {
        // TODO Auto-generated method stub
        return null;
    }
}
