package org.openstreetmap.josm.plugins.ods.entities.osm;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

public abstract class AbstractOsmEntityBuilder<T extends Entity> implements OsmEntityBuilder<T> {
    private LayerManager layerManager;
    private EntityStore<T> entityStore;
    private EntityType<T> entityType;
    private GeoUtil geoUtil;
    
    public AbstractOsmEntityBuilder(OdsModule module, EntityType<T> entityType) {
        super();
        this.geoUtil = module.getGeoUtil();
        this.layerManager = module.getOsmLayerManager();
        this.entityType = entityType;
        this.entityStore = layerManager.getEntityStore(entityType.getEntityClass());
    }

    @Override
    public Class<T> getEntityClass() {
        return entityType.getEntityClass();
    }

    public EntityType<T> getEntityType() {
        return entityType;
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
