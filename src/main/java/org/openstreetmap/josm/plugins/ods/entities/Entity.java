package org.openstreetmap.josm.plugins.ods.entities;

import java.util.List;
import java.util.Map;

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
	public String getSource();
	public String getSourceDate();
    boolean isIncomplete();
    public boolean isDeleted();
    public Object getReferenceId();
    public Long getPrimitiveId();
    public Geometry getGeometry();
    public void setGeometry(Geometry geometry);
    
    /**
    * Get the OSM primitive(s) from which this entity was constructed,
    * or that was/were constructed from this entity.
    * In most cases the list contains 1 item.
    *
    */
    public List<OsmPrimitive> getPrimitives();
    
    /**
     * Get the tags that are not associated with any of the entity's properties.
     */
    public Map<String, String> getOtherTags();
    
    public void setPrimitives(List<OsmPrimitive> primitives);
}
