package org.openstreetmap.josm.plugins.openservices.entities.imported;

import java.io.Serializable;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.metadata.MetaData;
import org.openstreetmap.josm.plugins.openservices.entities.AbstractEntity;


public class ImportedEntity extends AbstractEntity {
    private SimpleFeature feature;
    private MetaData metaData;
    
    public void setFeature(SimpleFeature feature) {
        this.feature = feature;
    }
    
    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public String getNamespace() {
        return feature.getName().getNamespaceURI().intern();
    }

    @Override
    public Serializable getId() {
        return feature.getID();
    }

    protected SimpleFeature getFeature() {
        return feature;
    }
    
    protected MetaData getMetaData() {
        return metaData;
    }
}
