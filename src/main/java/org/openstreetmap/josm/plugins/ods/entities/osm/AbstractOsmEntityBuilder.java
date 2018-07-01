package org.openstreetmap.josm.plugins.ods.entities.osm;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.storage.OsmEntityStore;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

public abstract class AbstractOsmEntityBuilder<T extends OsmEntity> implements OsmEntityBuilder<T> {
    private final OsmLayerManager layerManager;
    private final OsmEntityStore<T> entityStore;
    private final GeoUtil geoUtil;

    public AbstractOsmEntityBuilder(OsmLayerManager layerManager,
            OsmEntityStore<T> entityStore,
            GeoUtil geoUtil) {
        super();
        this.geoUtil = geoUtil;
        this.layerManager = layerManager;
        this.entityStore = entityStore;
    }

    public OsmEntityStore<T> getEntityStore() {
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
