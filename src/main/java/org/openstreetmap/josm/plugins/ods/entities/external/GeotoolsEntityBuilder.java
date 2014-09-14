package org.openstreetmap.josm.plugins.ods.entities.external;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public interface GeotoolsEntityBuilder<T extends Entity>  {
    public void buildGtEntity(SimpleFeature feature);
    public void setMetaData(MetaData metaData);
}
