package org.openstreetmap.josm.plugins.ods.entities.impl;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.EntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.storage.OdEntityStore;
import org.openstreetmap.josm.plugins.ods.osm.DefaultPrimitiveFactory;
import org.openstreetmap.josm.plugins.ods.osm.OsmPrimitiveFactory;

public abstract class AbstractEntityPrimitiveBuilder<T extends OdEntity>
implements EntityPrimitiveBuilder<T> {
    private final OdLayerManager layerManager;
    private final OsmPrimitiveFactory primitiveFactory;
    private final OdEntityStore<T, ?> entityStore;

    public AbstractEntityPrimitiveBuilder(OdLayerManager layerManager, OdEntityStore<T, ?> entityStore) {
        this.layerManager = layerManager;
        this.primitiveFactory = new DefaultPrimitiveFactory(layerManager);
        this.entityStore = entityStore;
    }

    @Override
    public void run() {
        entityStore.stream().filter(entity -> entity.getPrimitive() == null)
        .filter(entity -> entity.hide() == false)
        .forEach(this::createPrimitive);
    }

    @Override
    public void createPrimitive(T entity) {
        if (entity.getPrimitive() == null && entity.getGeometry() != null) {
            // TODO replace Map with something that handles null value neatly
            Map<String, String> tags = new HashMap<>();
            buildTags(entity, tags);
            OsmPrimitive primitive = primitiveFactory
                    .create(entity.getGeometry(), tags);
            entity.setPrimitive(primitive);
            layerManager.register(primitive, entity);
        }
    }

    protected abstract void buildTags(T entity, Map<String, String> tags);
}
