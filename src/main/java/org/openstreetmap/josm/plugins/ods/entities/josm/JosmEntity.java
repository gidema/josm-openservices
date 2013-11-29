package org.openstreetmap.josm.plugins.ods.entities.josm;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;

public abstract class JosmEntity extends AbstractEntity {
    protected OsmPrimitive primitive;
    private String namespace;
    private Map<String, String> otherKeys = new HashMap<String, String>();
    
    public JosmEntity(OsmPrimitive primitive) {
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
    
    public abstract String getSource();

}
