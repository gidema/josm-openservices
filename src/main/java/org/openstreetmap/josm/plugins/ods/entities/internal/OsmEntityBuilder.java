package org.openstreetmap.josm.plugins.ods.entities.internal;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface OsmEntityBuilder<T extends Entity> {
    public void buildOsmEntity(OsmPrimitive primitive);

}
