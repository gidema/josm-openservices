package org.openstreetmap.josm.plugins.ods.entities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.ods.crs.GeoUtil;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

/**
 * An ODS entity set contains all entities belonging to a WorkinSet.
 * 
 * @author gertjan
 *
 */
public class DefaultEntitySet implements EntitySet {
	private Map<String, EntityStore<? extends Entity>> stores = new HashMap<String, EntityStore<? extends Entity>>();
    private List<EntitySetListener> listeners = new LinkedList<EntitySetListener>();
    private Geometry boundary = null;
	
    public DefaultEntitySet() {
		// TODO Auto-generated constructor stub
	}
    
    public Geometry getBoundary() {
        return boundary;
    }

    public void addListener(EntitySetListener listener) {
    	listeners.add(listener);
    }
    
    public <T extends Entity> boolean add(T entity) {
    	@SuppressWarnings("unchecked")
		EntityStore<T> store = (EntityStore<T>) getStore(entity.getNamespace());
    	boolean added = store.add(entity);
    	if (added) {
//    		entity.setEntitySet(this);
            for (EntitySetListener listener : listeners) {
            	listener.entityAdded(entity);
            }
    	}
    	return added;
    }
    
    public <T extends Entity> EntityStore<T> getStore(String nameSpace) {
    	String ns = nameSpace.intern();
    	@SuppressWarnings("unchecked")
		EntityStore<T> store = (EntityStore<T>) stores.get(ns);
    	if (store == null) {
    		store = new EntityStore<T>(ns);
    		stores.put(ns,  store);
    	}
    	return store;
    }
    
    public void extendBoundary(Bounds bounds) {
        Polygon polygon = GeoUtil.getInstance().toPolygon(bounds);
        if (boundary == null) {
            boundary = polygon;
        }
        boundary = boundary.union(polygon);
    }
}
