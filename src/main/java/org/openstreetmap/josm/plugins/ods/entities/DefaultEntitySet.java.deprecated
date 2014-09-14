package org.openstreetmap.josm.plugins.ods.entities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

/**
 * An ODS entity set contains all entities belonging to a WorkingSet.
 * 
 * @author gertjan
 *
 */
public class DefaultEntitySet implements EntitySet {
	private Map<Class<? extends Entity>, EntityStore<? extends Entity>> stores = new HashMap<>();
    private Geometry boundary = null;
	
    public DefaultEntitySet() {
		// TODO Auto-generated constructor stub
	}
    
    @Override
    public Iterator<EntityStore<? extends Entity>> stores() {
        return stores.values().iterator();
    }

    public Geometry getBoundary() {
        return boundary;
    }

    public <T extends Entity> boolean add(T entity) {
        EntityStore<T> store = getStore(entity.getType());
        return store.add(entity);
    }
        
    public <T extends Entity> EntityStore<T> getStore(Class<? extends Entity> entityType) {
        @SuppressWarnings("unchecked")
        EntityStore<T> store = (EntityStore<T>) stores.get(entityType);
        if (store == null) {
            store = new EntityStore<T>();
            stores.put(entityType,  store);
        }
        return store;
    }
    
    public void extendBoundary(Bounds bounds) {
        Polygon polygon = GeoUtil.getInstance().toPolygon(bounds);
        extendBoundary(polygon);
    }

    @Override
    public void extendBoundary(Geometry boundary) {
        if (this.boundary == null) {
            this.boundary = boundary;
        }
        else {
            this.boundary = this.boundary.union(boundary);
        }
    }
}
