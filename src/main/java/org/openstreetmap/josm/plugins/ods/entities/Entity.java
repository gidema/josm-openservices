package org.openstreetmap.josm.plugins.ods.entities;

import java.util.Collection;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;

/**
 * ODS entities are represent entities like buildings, address nodes,
 * or streets. They are the interface between imported features and 
 * Josm entities.
 * Using these entities gives the possibility to build object relations
 * from geometric relations.
 * The method 'getPrimitives' creates the nodes, ways and relations to
 * represent this on a Josm layer.
 *   
 * @author gertjan
 *
 */
public interface Entity {
    /**
     * Create relations to other objects
     * @throws BuildException 
     */
    void build() throws BuildException;
    
	/**
	 * Get the entityType of this object
	 * 
	 * @return
	 */
	public Class<? extends Entity> getType();
	
	public boolean isInternal();
	
    boolean isIncomplete();

    public boolean isDeleted();
    
    /**
	 * Get the unique id of this entity with respect to its datasource
	 * 
	 * @return
	 */
	public Object getId();
	
	/**
	 * Get the unique name of this entity
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Set the containing entitySet property of this entity
	 * 
	 * @param entitySet
	 */
//	public void setEntitySet(EntitySet entitySet);
	
    /**
     * Get the containing entitySet of this entity
     * 
     * @return
     */
//    public EntitySet getEntitySet();
    
    
    /**
     * Create the osm primitives for this entity
     * 
     * @param primitiveBuilder
     * The primitiveBuilder to be used
     */
    public abstract void createPrimitives(PrimitiveBuilder primitiveBuilder);

    /**
     * Get the OSM primitives of this entity
     * In most cases, this collection will contain only one primitive
     * 
     * @return
     */
    public Collection<OsmPrimitive> getPrimitives();
}
