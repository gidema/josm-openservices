package org.openstreetmap.josm.plugins.ods.entities;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public interface EntityFactory {
    public Entity buildEntity(MetaData metaData, SimpleFeature feature) throws BuildException;

    public Entity buildEntity(String entityType, OsmPrimitive primitive)
            throws BuildException;
}
