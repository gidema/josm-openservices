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
	
	public String getSource();
	
	public boolean isInternal();
	
    boolean isIncomplete();

    public boolean isDeleted();
    
    public boolean hasReferenceId();
    
    public Object getReferenceId();
    
    public boolean hasGeometry();
    
    public Geometry getGeometry();
    /**
	 * Get the unique id of this entity with respect to its datasource
	 * 
	 * @return
	 */
	public <T> Comparable<T> getId();
	
	public boolean hasName();
	
	/**
	 * Get the unique name of this entity
	 * 
	 * @return
	 */
	public String getName();

    Collection<OsmPrimitive> getPrimitives();

	/**
	 * Set the containing environment property of this entity
	 * 
	 * @param environment
	 */
//	public void setEntitySet(EntitySet environment);
	
    /**
     * Get the containing environment of this entity
     * 
     * @return
     */
//    public EntitySet getEntitySet();
    
    
}
