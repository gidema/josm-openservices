package org.openstreetmap.josm.plugins.ods.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The EntityStore stores entities of a single entity type.
 * 
 * @author gertjan
 *
 */
public class EntityStore<T extends Entity> {
    private Map<Serializable, T> entities = new HashMap<Serializable, T>();
    private Map<String, T> namedEntities = new HashMap<String, T>();
	private String namespace;
    
	public EntityStore(String namespace) {
		super();
		this.namespace = namespace;
	}

	public String getNamespace() {
		return namespace;
	}
	
	public boolean add(T entity) {
		if (!entities.containsKey(entity.getId())) {
            entities.put(entity.getId(), entity);
      		namedEntities.put(entity.getName(), entity);
            return true;
		}
		return false;
	}
	
	public T get(Serializable id) {
		return entities.get(id);
	}
	
	public T getByName(String name) {
	    return namedEntities.get(name);
	}
	
	public Iterator<T> iterator() {
	    return entities.values().iterator();
	}
}
