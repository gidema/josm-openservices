package org.openstreetmap.josm.plugins.ods.entities.osm;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

public abstract class AbstractOsmEntityBuilder<T extends OsmEntity> implements OsmEntityBuilder<T> {
    private final OsmLayerManager layerManager;
    private final EntityStore<T> entityStore;
    private final Class<T> entityClass;
    private final GeoUtil geoUtil;

    public AbstractOsmEntityBuilder(OdsModule module, Class<T> entityClass) {
        super();
        this.geoUtil = module.getGeoUtil();
        this.layerManager = module.getOsmLayerManager();
        this.entityClass = entityClass;
        this.entityStore = layerManager.getEntityStore(entityClass);
    }

    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    public EntityStore<T> getEntityStore() {
        return entityStore;
    }

    public GeoUtil getGeoUtil() {
        return geoUtil;
    }

    protected void register(OsmPrimitive primitive, T entity) {
        entity.setPrimitive(primitive);
        entityStore.add(entity);
        layerManager.register(primitive, entity);
    }
}
