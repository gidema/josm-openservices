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
        if (osmDataLayer == null) {
            osmDataLayer = createOsmDataLayer();
            Main.main.addLayer(osmDataLayer);
        }
        return osmDataLayer;
    }
    
    protected OsmDataLayer createOsmDataLayer() {
        OsmDataLayer layer = new OsmDataLayer(new DataSet(), getName(), null);
        layer.setUploadDiscouraged(!isOsm());
        return layer;
    }

    public void initialize() {
        Layer oldLayer = null;
        if (Main.map != null) {
            oldLayer = Main.main.getActiveLayer();
        }
        this.getOsmDataLayer();
        if (oldLayer != null) {
            Main.map.mapView.setActiveLayer(oldLayer);
        }
    }
    
    @Override
    public <E extends Entity> EntityStore<E> getEntityStore(Class<E> clazz) {
        return entityStoreMap.get(clazz);
    }

    public void reset() {
        this.osmDataLayer.destroy();
        // Clear all data stores
        for (EntityStore<?> store : entityStoreMap.stores.values()) {
            store.clear();
        }
        nodeEntities.clear();
        wayEntities.clear();
        relationEntities.clear();
    }

    @Override
    public void deActivate() {
        this.reset();
        Main.map.mapView.removeLayer(osmDataLayer);
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
