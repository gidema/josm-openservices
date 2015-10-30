package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;

/**
 * <p>The LayerManager manages the Osm Datalayer that is shown in the 
 * user interface.</p>
 * <p>A LayerManager object stay active during the life time of the Josm application
 * whereas the Osm Datalayer maybe closed and the ODS datastores may be cleared.
 * 
 * @author gertjan
 *
 */
public interface LayerManager {
    /**
     * Get the current Osm Datalayer. Create a new one if no current datalayer is
     * available.
     * 
     * @return The Osm datalayer
     */
    public OsmDataLayer getOsmDataLayer();
    
    /**
     * @return true if the underlying datalayer contains data from the OSM server.
     *     false if it contains data from an open data service
     */
    public boolean isOsm();
    
    /**
     * Remove the underlying Osm Datalayer and clear the ODS data stores
     */
    public void reset();
    
    public void register(OsmPrimitive primitive, Entity entity);
    /**
     * Get the Entity related to the given OsmPrimitive
     * 
     * @param primitive
     * @return
     */
    public Entity getEntity(OsmPrimitive primitive);
    
    /**
     * Get the entity store for the given Entity type
     * 
     * @param clazz The clazz of the Entity type
     * @return The store for this Entity type
     */
    public <E extends Entity> EntityStore<E> getEntityStore(Class<E> clazz);

}
