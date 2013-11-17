package org.openstreetmap.josm.plugins.openservices.entities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.openservices.crs.CRSUtil;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Place;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Street;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

/**
 * An ODS entity set contains all entities belonging to a WorkinSet.
 * 
 * @author gertjan
 *
 */
public class EntitySet {
	private Map<String, EntityStore<? extends Entity>> stores = new HashMap<>();
    private List<EntitySetListener> listeners = new LinkedList<>();
    private Geometry boundary = null;
	
    public EntitySet() {
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
    		entity.setEntitySet(this);
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
    
    public EntityStore<Building> getBuildings() {
    	return getStore(Building.NAMESPACE);
    }
    
    public EntityStore<Place> getPlaces() {
    	return getStore(Place.NAMESPACE);
    }
    
    public EntityStore<Street> getStreets() {
    	return getStore(Street.NAMESPACE);
    }
    
    public void extendBoundary(Bounds bounds) {
        Polygon polygon = CRSUtil.toPolygon(bounds);
        if (boundary == null) {
            boundary = polygon;
        }
        boundary = boundary.union(polygon);
    }
}
