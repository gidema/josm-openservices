package org.openstreetmap.josm.plugins.ods.entities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.index.quadtree.Quadtree;

/**
 * The EntityStore stores entities of a single entity type.
 * 
 * @author gertjan
 *
 */
public class EntityStore<T extends Entity> {
    // TODO Add a way to configure whether an entity has a name
    // and if it should be indexed
    private Map<Object, T> entities = new HashMap<>();
    private Map<Object, T> referencedEntities = new HashMap<>();
    private Map<String, T> namedEntities = new HashMap<>();
    private Quadtree index = new Quadtree();
	
	public boolean add(T entity) {
		if (!entities.containsKey(entity.getId())) {
            entities.put(entity.getId(), entity);
            if (entity.hasName()) {
      		    namedEntities.put(entity.getName(), entity);
            }
      		if (entity.hasGeometry()) {
      		    index.insert(entity.getGeometry().getEnvelopeInternal(), entity);
      		}
      		if (entity.hasReferenceId()) {
      		    referencedEntities.put(entity.getReferenceId(), entity);
      		}
            return true;
		}
		return false;
	}
	
	public T get(Object id) {
		return entities.get(id);
	}
	
    public T getByReference(Object id) {
        return referencedEntities.get(id);
    }
    
	public T getByName(String name) {
	    return namedEntities.get(name);
	}
	
	public Iterator<T> iterator() {
	    return entities.values().iterator();
	}

    public boolean contains(T entity) {
        return get(entity.getId()) != null;
    }
    
    public void remove(T entity) {
        entities.remove(entity.getId());
    }
    
    public List<T> query(Geometry geometry) {
        return query(geometry, null);
    }
    
    public List<T> query(Geometry geometry, Double tolerance) {
        Envelope envelope = geometry.getEnvelopeInternal();
        if (tolerance != null) {
            envelope.expandBy(tolerance);
        }
        @SuppressWarnings("unchecked")
        List<T> result = index.query(envelope);
        return result;
    }
}
