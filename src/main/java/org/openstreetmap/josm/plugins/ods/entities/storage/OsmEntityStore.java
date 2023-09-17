package org.openstreetmap.josm.plugins.ods.entities.storage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

/**
 * The abstract OsmEntityStore stores osm entities of a single entity type.
 * The entity's primitive id is used as the primary index.
 * There is a GeoIndex to find entities by LatLon coordinates.  
 * 
 * @author gertjan
 *
 */
public abstract class OsmEntityStore<T extends OsmEntity> implements EntityStore<T> {
    private final Map<Long, T> index;
    private final GeoIndex<T> geoIndex;
    
    public OsmEntityStore() {
        super();
        index = new HashMap<>();
        geoIndex = new GeoIndexImpl<>();
    }

    public GeoIndex<T> getGeoIndex() {
        return geoIndex;
    }
    
    @Override
    public void add(T entity) {
        if (index.put(entity.getPrimitiveId(), entity) == null) {
            geoIndex.insert(entity);
            onAdd(entity);
        }
    }

    public boolean contains(Long primitiveId) {
        return index.containsKey(primitiveId);
    }
    
    @Override
    public Stream<T> stream() {
        return index.values().stream();
    }

    @Override
    public void remove(T entity) {
        geoIndex.remove(entity);
        index.remove(entity.getPrimitiveId());
        onRemove(entity);
    }

    @Override
    public void clear() {
        beforeClear();
        geoIndex.clear();
        index.clear();
    }
    
    @Override
    public Iterator<T> iterator() {
        return index.values().iterator();
    }

    public abstract void onAdd(T entity);
    
    public abstract void onRemove(T entity);
    
    public abstract void beforeClear();
}
