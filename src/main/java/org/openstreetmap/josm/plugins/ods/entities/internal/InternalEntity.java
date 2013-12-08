package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;

public abstract class InternalEntity extends AbstractEntity {
    protected OsmPrimitive primitive;
    private Map<String, String> otherKeys = new HashMap<>();
    
    public InternalEntity(OsmPrimitive primitive) {
        super();
        this.primitive = primitive;
    }

    public OsmPrimitive getPrimitive() {
        return primitive;
    }
    
    @Override
    public Collection<OsmPrimitive> getPrimitives() {
        return Collections.singleton(primitive);
    }
    
    @Override
    public Serializable getId() {
        return primitive.getId();
    }
    
    protected Map<String, String> getOtherKeys() {
        return otherKeys;
    }
    
    public abstract String getSource();

    @Override
    public void createPrimitives(PrimitiveBuilder primitiveBuilder) {
        // No need to create primitives for a InternalEntity
    }

}
