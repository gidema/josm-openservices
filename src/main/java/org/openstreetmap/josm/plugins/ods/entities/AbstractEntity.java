package org.openstreetmap.josm.plugins.ods.entities;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;


public abstract class AbstractEntity implements Entity {
//    private EntitySet entitySet;
    private Collection<OsmPrimitive> primitives;
    
	@Override
	public String getName() {
		return null;
	}

//	@Override
//	public void setEntitySet(EntitySet entitySet) {
//		this.entitySet = entitySet;
//	}

//	@Override
//	public EntitySet getEntitySet() {
//		return entitySet;
//	}
	
	
	protected void setPrimitives(Collection<OsmPrimitive> primitives) {
        this.primitives = primitives;
    }

    @Override
    public Collection<OsmPrimitive> getPrimitives() {
        return primitives;
    }

    protected Map<String, String> getKeys() {
	    return new HashMap<>();
	}
}
