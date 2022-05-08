package org.openstreetmap.josm.plugins.ods.entities.opendata;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.AbstractLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;

/**
 * The OdLayerManager manages the layer containing the data that has been
 * imported from an open data source.
 * As opposed to the OsmDataLayerManager that manages data from
 * the OSM server.
 *
 * @author Gertjan Idema
 *
 */
public class OdLayerManager extends AbstractLayerManager {
    private final Map<Long, OdEntity> nodeEntities = new HashMap<>();
    private final Map<Long, OdEntity> wayEntities = new HashMap<>();
    private final Map<Long, OdEntity> relationEntities = new HashMap<>();

    public OdLayerManager(String name) {
        super(name);
    }

    @Override
    public boolean isOsm() {
        return false;
    }

    public void register(OsmPrimitive primitive, OdEntity entity)
    {
        switch (primitive.getType()) {
        case NODE:
            nodeEntities.put(primitive.getUniqueId(), entity);
            break;
        case WAY:
            wayEntities.put(primitive.getUniqueId(), entity);
            break;
        case RELATION:
            relationEntities.put(primitive.getUniqueId(), entity);
            break;
        default:
            break;
        }
    }

    /**
     * Get the Entity related to the given OsmPrimitive
     *
     * @param primitive
     * @return
     */
    public OdEntity getEntity(OsmPrimitive primitive) {
        switch (primitive.getType()) {
        case NODE:
            return nodeEntities.get(primitive.getUniqueId());
        case WAY:
            return wayEntities.get(primitive.getUniqueId());
        case RELATION:
            return relationEntities.get(primitive.getUniqueId());
        default:
            return null;
        }
    }

    @Override
    public void deActivate() {
        if (isActive()) {
            // Clear all data stores
            nodeEntities.clear();
            wayEntities.clear();
            relationEntities.clear();
        }
        super.deActivate();
    }
}
