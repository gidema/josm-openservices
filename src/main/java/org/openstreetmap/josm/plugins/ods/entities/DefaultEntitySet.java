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
 * An ODS entity set contains all entities belonging to a WorkingSet.
 * 
 * @author gertjan
 *
 */
public class DefaultEntitySet implements EntitySet {
	private Map<String, EntityStore> stores = new HashMap<>();
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
    
    public boolean add(Entity entity) {
		EntityStore store = getStore(entity.getType());
    	return store.add(entity);
    }
    
    public EntityStore getStore(String entityType) {
    	String ns = entityType;
		EntityStore store = stores.get(ns);
    	if (store == null) {
    		store = new EntityStore(ns);
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
