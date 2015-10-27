package org.openstreetmap.josm.plugins.ods.entities.managers;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.opendata.OpenDataAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.opendata.OpenDataBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.osm.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.osm.OsmBuildingStore;

public class DataManager {
    private EntityStoreMap osmEntityStores = new EntityStoreMap();
    private EntityStoreMap openDataEntityStores = new EntityStoreMap();
    private OpenDataBuildingStore openDataBuildingStore = new OpenDataBuildingStore();
    private OsmBuildingStore osmBuildingStore = new OsmBuildingStore();
    private OpenDataAddressNodeStore openDataAddressNodeStore = new OpenDataAddressNodeStore();
    private OsmAddressNodeStore osmAddressNodeStore = new OsmAddressNodeStore();

    public DataManager() {
        super();
        // TODO register stores from module
        osmEntityStores.put(Building.class, osmBuildingStore);
        osmEntityStores.put(AddressNode.class, osmAddressNodeStore);
        openDataEntityStores.put(Building.class, openDataBuildingStore);
        openDataEntityStores.put(AddressNode.class, openDataAddressNodeStore);
    }

    public <E extends Entity> EntityStore<E> getOsmEntityStore(Class<E> clazz) {
        return osmEntityStores.get(clazz);
    }
    
    public <E extends Entity> EntityStore<E> getOpenDataEntityStore(Class<E> clazz) {
        return openDataEntityStores.get(clazz);
    }

    private class EntityStoreMap {
        private Map<Class<?>, Object> stores = new HashMap<Class<?>, Object>();

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
