package org.openstreetmap.josm.plugins.ods.entities.osm;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractGeoEntityStore;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

public abstract class AbstractOsmEntityBuilder<T extends OsmEntity> implements OsmEntityBuilder<T> {
    private final OsmLayerManager layerManager;
    private final Class<T> entityClass;
    private final GeoUtil geoUtil;

    public AbstractOsmEntityBuilder(OdsContext context, Class<T> entityClass) {
        super();
        this.geoUtil = context.getComponent(GeoUtil.class);
        this.layerManager = context.getComponent(OsmLayerManager.class);
        this.entityClass = entityClass;
    }

    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    abstract public AbstractGeoEntityStore<T> getEntityStore();

    public GeoUtil getGeoUtil() {
        return geoUtil;
    }

    protected void register(OsmPrimitive primitive, T entity) {
        entity.setPrimitive(primitive);
        getEntityStore().add(entity);
        layerManager.register(primitive, entity);
    }
}
