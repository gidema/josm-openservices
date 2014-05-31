package org.openstreetmap.josm.plugins.ods.osm;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface PrimitiveFactory {
    OsmPrimitive[] buildPrimitives(Entity entity);
}
