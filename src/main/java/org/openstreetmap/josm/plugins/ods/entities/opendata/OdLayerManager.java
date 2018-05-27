package org.openstreetmap.josm.plugins.ods.entities.opendata;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.AbstractLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;

/**
 * The OdLayerManager manages the layer containing the data that has been
 * imported from an open data source.
 * As opposed to the OsmDataLayerManager that manages data from
 * the OSM server.
 *
 * @author Gertjan Idema
 *
 */
public class OdLayerManager extends AbstractLayerManager {
    private final Map<Long, OdEntity> nodeEntities = new HashMap<>();
    private final Map<Long, OdEntity> wayEntities = new HashMap<>();
    private final Map<Long, OdEntity> relationEntities = new HashMap<>();
    private final EntityStoreMap entityStoreMap = new EntityStoreMap();

    public OdLayerManager(String name) {
        super(name);
    }

    @Override
    public boolean isOsm() {
        return false;
    }

    public void register(OsmPrimitive primitive, OdEntity entity)
    {
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

    public <T extends OdEntity> void addEntityStore(Class<T> clazz, EntityStore<T> entityStore) {
        this.entityStoreMap.put(clazz, entityStore);
    }

    /**
     * Get the Entity related to the given OsmPrimitive
     *
     * @param primitive
     * @return
     */
    public OdEntity getEntity(OsmPrimitive primitive) {
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
    public <E extends OdEntity> EntityStore<E> getEntityStore(Class<E> clazz) {
        return entityStoreMap.get(clazz);
    }

    @Override
    public void deActivate() {
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
        Map<Class<?>, EntityStore<? extends OdEntity>> stores = new HashMap<>();

        public EntityStoreMap() {
            // TODO Auto-generated constructor stub
        }

        public <T extends OdEntity> void put(Class<T> clazz,
                EntityStore<? extends T> store) {
            stores.put(clazz, store);
        }

        @SuppressWarnings("unchecked")
        public <T extends OdEntity> EntityStore<T> get(Class<T> clazz) {
            return (EntityStore<T>) stores.get(clazz);
        }
    }
}
