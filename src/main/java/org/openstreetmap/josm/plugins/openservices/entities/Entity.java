package org.openstreetmap.josm.plugins.openservices.entities;

import java.io.Serializable;
import java.util.Collection;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.openservices.PrimitiveBuilder;

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
	public String getNamespace();
	public Serializable getId();
	public String getName();
	public void setEntitySet(EntitySet entitySet);
    public EntitySet getEntitySet();
    public void createPrimitives(PrimitiveBuilder primitiveBuilder);
    public Collection<OsmPrimitive> getPrimitives();
}
