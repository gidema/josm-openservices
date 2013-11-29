package org.openstreetmap.josm.plugins.openservices.entities.imported;

import java.io.Serializable;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.openservices.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.openservices.entities.AbstractEntity;
import org.openstreetmap.josm.plugins.openservices.entities.BuildException;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaData;


public abstract class ImportedEntity extends AbstractEntity {
    private SimpleFeature feature;
    private MetaData metaData;
    private String namespace;
    
    public void init(MetaData metaData) throws BuildException {
        namespace = feature.getName().getNamespaceURI().intern();
    }
    
    public void setFeature(SimpleFeature feature) {
        this.feature = feature;
    }
    
    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public String getNamespace() {
        return namespace;
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
    
    public abstract void createPrimitives(PrimitiveBuilder primitiveBuilder);

}
