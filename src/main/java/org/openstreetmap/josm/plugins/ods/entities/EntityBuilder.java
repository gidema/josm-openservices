package org.openstreetmap.josm.plugins.ods.entities;

import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public interface EntityBuilder<T, U extends Entity> {
    public Entity build(T data, MetaData metaData);
}
