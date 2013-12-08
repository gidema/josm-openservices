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
public class EntityStore {
    private Map<Serializable, Entity> entities = new HashMap<>();
    private Map<String, Entity> namedEntities = new HashMap<>();
	private String namespace;
    
	public EntityStore(String namespace) {
		super();
		this.namespace = namespace;
	}

	public String getNamespace() {
		return namespace;
	}
	
	public boolean add(Entity entity) {
		if (!entities.containsKey(entity.getId())) {
            entities.put(entity.getId(), entity);
      		namedEntities.put(entity.getName(), entity);
            return true;
		}
		return false;
	}
	
	public Entity get(Serializable id) {
		return entities.get(id);
	}
	
	public Entity getByName(String name) {
	    return namedEntities.get(name);
	}
	
	public Iterator<Entity> iterator() {
	    return entities.values().iterator();
	}

    public boolean contains(Entity entity) {
        return get(entity.getId()) != null;
    }
}
