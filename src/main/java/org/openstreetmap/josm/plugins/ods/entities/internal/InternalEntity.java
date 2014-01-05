package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface InternalEntity extends Entity {
    public OsmPrimitive getPrimitive();
    
    public Map<String, String> getOtherKeys();
}
