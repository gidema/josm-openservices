package org.openstreetmap.josm.plugins.ods.entities;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public interface EntityType<T extends Entity> {
    public boolean recognize(OsmPrimitive primitive);
    public Class<T> getEntityClass();
}
