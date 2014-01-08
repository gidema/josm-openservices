package org.openstreetmap.josm.plugins.ods.entities;

import java.util.Collection;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

import com.vividsolutions.jts.geom.Geometry;

/**
 * ODS entities are represent entities like buildings, address nodes,
 * or streets. They are the interface between imported features and 
 * Josm entities.
 * Using these entities gives the possibility to build object relations
 * from geometric relations.
 * The method 'getPrimitives' returns the nodes, ways and relations to
 * represent this entity on a Josm layer.
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
	
	public String getSource();
	
	public boolean isInternal();
	
    boolean isIncomplete();

    public boolean isDeleted();
    
    public boolean hasGeometry();
    
    public Geometry getGeometry();
    /**
	 * Get the unique id of this entity with respect to its datasource
	 * 
	 * @return
	 */
	public Object getId();
	
	/**
	 * Returns whether or not this entity has a domain specific reference.
	 * @return
	 */
	public boolean hasReferenceId();
	
	/**
	 * Returns the domain specific referenceId of this entity
	 * @return
	 */
	public Object getReferenceId();
	
	/**
	 * Returns whether or not this entity has a name.
	 * @return
	 */
	public boolean hasName();
	
	/**
	 * Get the unique name of this entity if applicable
	 * 
	 * @return
	 */
	public String getName();

    public Collection<OsmPrimitive> getPrimitives();
}
