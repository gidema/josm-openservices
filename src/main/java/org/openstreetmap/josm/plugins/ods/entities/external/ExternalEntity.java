package org.openstreetmap.josm.plugins.ods.entities.external;

import java.io.Serializable;
import java.util.Collection;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

import com.vividsolutions.jts.geom.Geometry;


public abstract class ExternalEntity extends AbstractEntity {
    private SimpleFeature feature;
    private MetaData metaData;
    
    public void init(MetaData metaData) throws BuildException {
        // entityType = feature.getName().getNamespaceURI().intern();
    }
    
    @Override
    public boolean isInternal() {
        return false;
    }

    public void setFeature(SimpleFeature feature) {
        this.feature = feature;
    }
    
    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
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
    
    @Override
    public void createPrimitives(PrimitiveBuilder builder) {
        if (getPrimitives() == null && getGeometry() != null) {
            Collection<OsmPrimitive> primitives = builder.build(getGeometry());
            for (OsmPrimitive primitive : primitives) {
                buildTags(primitive);
            }
        }
    }
    
    public abstract Geometry getGeometry();
    
    protected abstract void buildTags(OsmPrimitive primitive);

}
