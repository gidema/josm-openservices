package org.openstreetmap.josm.plugins.ods;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.UploadPolicy;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;

/**
 * 
 * @author Gertjan Idema
 * 
 */
public abstract class AbstractLayerManager implements LayerManager {
    private String name;
    private OsmDataLayer osmDataLayer;
    private Map<Long, Entity> nodeEntities = new HashMap<>();
    private Map<Long, Entity> wayEntities = new HashMap<>();
    private Map<Long, Entity> relationEntities = new HashMap<>();
    private EntityStoreMap entityStoreMap = new EntityStoreMap();
    private boolean active = false;

    public AbstractLayerManager(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public <T extends Entity> void addEntityStore(Class<T> clazz, EntityStore<T> entityStore) {
        this.entityStoreMap.put(clazz, entityStore);
    }
    
    @Override
    public OsmDataLayer getOsmDataLayer() {
        return osmDataLayer;
    }
    
    protected OsmDataLayer createOsmDataLayer() {
        DataSet dataSet = new DataSet();
        OsmDataLayer layer = new OsmDataLayer(dataSet, getName(), null);
        if (!isOsm()) {
            dataSet.setUploadPolicy(UploadPolicy.BLOCKED);
        }
        return layer;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }
    
    public void activate() {
        if (!active) {
            Layer oldLayer = null;
            if (MainApplication.getMap() != null) {
                oldLayer = MainApplication.getLayerManager().getActiveLayer();
            }
            osmDataLayer = createOsmDataLayer();
            MainApplication.getLayerManager().addLayer(osmDataLayer);
            if (oldLayer != null) {
                MainApplication.getLayerManager().setActiveLayer(oldLayer);
            }
            this.active = true;
        }
    }
    
    @Override
    public <E extends Entity> EntityStore<E> getEntityStore(Class<E> clazz) {
        return entityStoreMap.get(clazz);
    }

    @Override
    public void reset() {
        if (isActive()) {
            deActivate();
        }
        activate();
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
            active = false;
            if (MainApplication.getLayerManager().containsLayer(this.osmDataLayer)) {
                MainApplication.getLayerManager().removeLayer(this.osmDataLayer);
            }
        }
    }

    @Override
    public void register(OsmPrimitive primitive, Entity entity) {
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

    @Override
    public Entity getEntity(OsmPrimitive primitive) {
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
    
    private class EntityStoreMap {
        Map<Class<?>, EntityStore<? extends Entity>> stores = new HashMap<>();

        public EntityStoreMap() {
            // TODO Auto-generated constructor stub
        }

        public <T extends Entity> void put(Class<T> clazz,
                EntityStore<? extends T> store) {
            stores.put(clazz, store);
        }

        @SuppressWarnings("unchecked")
        public <T extends Entity> EntityStore<T> get(Class<T> clazz) {
            return (EntityStore<T>) stores.get(clazz);
        }
    }
}
