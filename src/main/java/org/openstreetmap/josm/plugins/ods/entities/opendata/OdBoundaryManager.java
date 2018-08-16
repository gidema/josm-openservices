package org.openstreetmap.josm.plugins.ods.entities.opendata;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.entities.storage.OdEntityStore;

import com.vividsolutions.jts.geom.Geometry;

public class OdBoundaryManager {
    private final List<OdEntityStore<?,?>> entityStores;

    public OdBoundaryManager(List<OdEntityStore<?, ?>> entityStores) {
        super();
        this.entityStores = entityStores;
    }

    public void reset() {
        entityStores.forEach(store -> store.clear());
    }

    public void update(Geometry bounds) {
        entityStores.forEach(store -> store.extendBoundary(bounds));
    }
}
