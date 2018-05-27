package org.openstreetmap.josm.plugins.ods.entities.osm;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.AbstractLayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

/**
 * The OsmLayerManager manager the layer containing the data that has been
 * down loaded from the OSM server.
 *
 * @author Gertjan Idema
 *
 */
public class OsmLayerManager extends AbstractLayerManager {
    private final OsmEntitiesBuilder entitiesBuilder;

    private final Map<Long, OsmEntity> nodeEntities = new HashMap<>();
    private final Map<Long, OsmEntity> wayEntities = new HashMap<>();
    private final Map<Long, OsmEntity> relationEntities = new HashMap<>();
    private final EntityStoreMap entityStoreMap = new EntityStoreMap();

    public OsmLayerManager(OdsModule module, String name) {
        super(name);
        this.entitiesBuilder = new OsmEntitiesBuilder(module);
    }

    @Override
    public boolean isOsm() {
        return true;
    }

    public OsmEntitiesBuilder getEntitiesBuilder() {
        return entitiesBuilder;
    }
    public <T extends OsmEntity> void addEntityStore(Class<T> clazz, EntityStore<T> entityStore) {
        this.entityStoreMap.put(clazz, entityStore);
    }

    public void register(OsmPrimitive primitive, OsmEntity entity) {
        switch (primitive.getType()) {
        case NODE:
            nodeEntities.put(primitive.getUniqueId(), entity);
            break;
        case WAY:
            wayEntities.put(primitive.getUniqueId(), entity);
            break;
        case RELATION:
            relationEntities.put(primitive.getUniqueId(), entity);
            break;
        default:
            break;
        }
    }

    /**
     * Get the Entity related to the given OsmPrimitive
     *
     * @param primitive
     * @return
     */
    public OsmEntity getEntity(OsmPrimitive primitive) {
        switch (primitive.getType()) {
        case NODE:
            return nodeEntities.get(primitive.getUniqueId());
        case WAY:
            return wayEntities.get(primitive.getUniqueId());
        case RELATION:
            return relationEntities.get(primitive.getUniqueId());
        default:
            return null;
        }
    }


    /**
     * Get the entity store for the given Entity type
     *
     * @param clazz The clazz of the Entity type
     * @return The store for this Entity type
     */
    public <E extends OsmEntity> EntityStore<E> getEntityStore(Class<E> clazz) {
        return entityStoreMap.get(clazz);
    }

    @Override
    public void deActivate() {
        super.deActivate();
        if (isActive()) {
            // Clear all data stores
            for (EntityStore<?> store : entityStoreMap.stores.values()) {
                store.clear();
            }
            nodeEntities.clear();
            wayEntities.clear();
            relationEntities.clear();
        }
        super.deActivate();
    }


    private class EntityStoreMap {
        Map<Class<?>, EntityStore<? extends OsmEntity>> stores = new HashMap<>();

        public EntityStoreMap() {
            // TODO Auto-generated constructor stub
        }

        public <T extends OsmEntity> void put(Class<T> clazz,
                EntityStore<? extends T> store) {
            stores.put(clazz, store);
        }

        @SuppressWarnings("unchecked")
        public <T extends OsmEntity> EntityStore<T> get(Class<T> clazz) {
            return (EntityStore<T>) stores.get(clazz);
        }
    }

}
