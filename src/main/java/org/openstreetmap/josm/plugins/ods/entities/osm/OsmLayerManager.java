package org.openstreetmap.josm.plugins.ods.entities.osm;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.AbstractLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractGeoEntityStore;

/**
 * The OsmLayerManager manages the layer containing the data that has been
 * down loaded from the OSM server.
 *
 * @author Gertjan Idema
 *
 */
public class OsmLayerManager extends AbstractLayerManager {
    private final Map<Long, OsmEntity> nodeEntities = new HashMap<>();
    private final Map<Long, OsmEntity> wayEntities = new HashMap<>();
    private final Map<Long, OsmEntity> relationEntities = new HashMap<>();

    public OsmLayerManager(String name) {
        super(name);
    }

    @Override
    public boolean isOsm() {
        return true;
    }

    public void register(OsmPrimitive primitive, OsmEntity entity) {
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
    public OsmEntity getEntity(OsmPrimitive primitive) {
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
        super.deActivate();
        if (isActive()) {
            nodeEntities.clear();
            wayEntities.clear();
            relationEntities.clear();
        }
        super.deActivate();
    }
}
