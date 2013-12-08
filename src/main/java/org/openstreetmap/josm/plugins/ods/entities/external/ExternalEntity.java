package org.openstreetmap.josm.plugins.ods.entities.external;

import java.io.Serializable;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

import com.vividsolutions.jts.geom.Geometry;


public abstract class ExternalEntity extends AbstractEntity {
    private SimpleFeature feature;
    private MetaData metaData;
    private String entityType;
    
    public void init(MetaData metaData) throws BuildException {
        entityType = feature.getName().getNamespaceURI().intern();
    }
    
    public void setFeature(SimpleFeature feature) {
        this.feature = feature;
    }
    
    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public String getType() {
        return entityType;
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
    
    public abstract Geometry getGeometry();
    
    @Override
    public void createPrimitives(PrimitiveBuilder builder) {
        if (getPrimitives() == null && getGeometry() != null) {
            setPrimitives(builder.build(getGeometry(), getKeys()));
        }
    }

}
