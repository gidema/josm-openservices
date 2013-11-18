package org.openstreetmap.josm.plugins.openservices.entities.josm;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.openservices.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.openservices.entities.AbstractEntity;
import org.openstreetmap.josm.plugins.openservices.entities.EntitySet;

public class JosmEntity extends AbstractEntity {
    private OsmPrimitive primitive;
    private String namespace;
    private EntitySet entitySet;
    private Map<String, String> otherKeys = new HashMap<>();
    
    public JosmEntity(OsmPrimitive primitive) {
        super();
        this.primitive = primitive;
    }

    @Override
    public void setEntitySet(EntitySet entitySet) {
        this.entitySet = entitySet;
    }
    
    @Override
    public EntitySet getEntitySet() {
        return entitySet;
    }

    @Override
    public void createPrimitives(PrimitiveBuilder primitiveBuilder) {
        // Irrelevant TODO remove from Entity interface?
    }

    public OsmPrimitive getPrimitive() {
        return primitive;
    }
    
    @Override
    public Collection<OsmPrimitive> getPrimitives() {
        return Collections.singleton(primitive);
    }
    
    @Override
    public String getNamespace() {
        if (namespace == null) {
            namespace = "JOSM." + primitive.getType();
        }
         return namespace;
    }

    @Override
    public Serializable getId() {
        return primitive.getId();
    }
    
    protected Map<String, String> getOtherKeys() {
        return otherKeys;
    }

}
