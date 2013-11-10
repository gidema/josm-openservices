package org.openstreetmap.josm.plugins.openservices.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The EntityStore stores entities of a single entity type.
 * 
 * @author gertjan
 *
 */
public class EntityStore<T extends Entity> {
    private Map<Serializable, T> entities = new HashMap<>();
    private Map<String, List<T>> namedEntities = new HashMap<>();
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
            String name = entity.getName();
            if (name != null) {
            	List<T> list = getByName(name);
            	if (list == null) {
            		list = new LinkedList<T>();
            		namedEntities.put(name, list);
            	}
            	list.add(entity);
            }
            return true;
		}
		return false;
	}
	
	public T get(Serializable id) {
		return entities.get(id);
	}
	
	public List<T> getByName(String name) {
	    List<T> entities = namedEntities.get(name);
	    if (entities == null) {
	        entities = new LinkedList<T>();
	        namedEntities.put(name,  entities);
	    }
	    return entities;
	}
	
	public Iterator<T> iterator() {
	    return entities.values().iterator();
	}
}
