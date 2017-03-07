package org.openstreetmap.josm.plugins.ods;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
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
    
    public OsmDataLayer getOsmDataLayer() {
        return osmDataLayer;
    }
    
    protected OsmDataLayer createOsmDataLayer() {
        OsmDataLayer layer = new OsmDataLayer(new DataSet(), getName(), null);
        layer.setUploadDiscouraged(!isOsm());
        return layer;
    }

    public boolean isActive() {
        return this.active;
    }
    
    public void activate() {
        if (!active) {
            Layer oldLayer = null;
            if (Main.map != null) {
                oldLayer = Main.getLayerManager().getActiveLayer();
            }
            osmDataLayer = createOsmDataLayer();
            Main.getLayerManager().addLayer(osmDataLayer);
            if (oldLayer != null) {
                Main.getLayerManager().setActiveLayer(oldLayer);
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
            // Clear all data stores
            for (EntityStore<?> store : entityStoreMap.stores.values()) {
                store.clear();
            }
            nodeEntities.clear();
            wayEntities.clear();
            relationEntities.clear();
            deActivate();
            activate();
//            if (Main.getLayerManager().containsLayer(osmDataLayer)) {
//                Main.getLayerManager().removeLayer(osmDataLayer);
//            }
//            osmDataLayer = createOsmDataLayer();
//            
//           this.osmDataLayer.data.clear();
//            this.osmDataLayer.data.getDataSources().clear();
//            if (!Main.getLayerManager().containsLayer(osmDataLayer)) {
//                Main.getLayerManager().addLayer(osmDataLayer);
//            }
        }
    }

    @Override
    public void deActivate() {
        if (isActive()) {
            active = false;
            this.reset();
            Main.getLayerManager().removeLayer(this.osmDataLayer);
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
        private Map<Class<?>, EntityStore<? extends Entity>> stores = new HashMap<>();

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
