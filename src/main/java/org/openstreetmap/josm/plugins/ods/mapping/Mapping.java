package org.openstreetmap.josm.plugins.ods.mapping;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.update.UpdateTaskType;

/**
 * A Mapping registers a mapping between 1 or more OpenData entities and 1 or more Osm entities.
 * Most mappings are 1-on-1, but there are special cases:
 * - Mistakenly duplicates in Osm
 * - Purposeful duplicate in Osm. For example multiple POIs at the same address
 * - Complex mappings. A single  OpenData entity is represented by multiple Osm entities or
 *   vise versa.
 * Ideally, the mapping of entities can be based on a unique id that is represented in Osm by a
 * ref:? tag. This is not always possible. A unique key may not be defined in the data source,
 * or it may not be available through an interface that can be accessed form Josm.
 * Even if a unique key is available, the community may have chosen not to tag the key in Osm.
 * When no unique id can be used, a surrogate key must be assigned to the mapping and entity
 * mapping can be base on other (softer) criteria like addresses or proximity.
 *   
 * Please note this interface used to be called Match in the past 
 * 
 * @author gertjan
 *
 * @param <T1> The type of the Osm entity
 * @param <T2> The type of the OpenData entity
 */
public interface Mapping<T1 extends OsmEntity, T2 extends OdEntity> {
    final static AtomicLong idCounter = new AtomicLong(0);

    public Object getId();
    
    /**
     * A mapping is considered simple, if it contains at most one OSM
     * entity and at most one Open Data entity
     *
     * @return true if this is a simple mapping
     */
    boolean isSimple();

    /**
     * A two way mapping has at least 1 one OSM entity and at least 1 Open Data entity
     *
     * @return true if this is a two way mapping
     */
    boolean isTwoWay();
    
    /**
     * Get the first (only) Osm entity in this mapping
     * TODO Should we enforce throwing an exception in the case of multiple Osm entities?
     *  
     * @return
     */
    public T1 getOsmEntity();

    /**
     * Get the first (only) OpenData entity in this mapping
     * TODO Should we enforce throwing an exception in the case of multiple OpenData entities?
     *  
     * @return
     */
    public T2 getOpenDataEntity();

    public List<? extends T1> getOsmEntities();

    public List<? extends T2> getOpenDataEntities();

    public <E extends T1> void addOsmEntity(E entity);

    public <E extends T2> void addOpenDataEntity(E entity);
    
    public void analyze();

    public void refreshUpdateTags();

    static Long generateUniqueId() {
        return idCounter.decrementAndGet();
    }
    
    /**
     * Get the update task type that is available to update the OSM objects.   
     * 
     * @return The UpdateTaskType or null is no update task is available
     */
//    public UpdateTaskType getUpdateTaskType();
}
