package org.openstreetmap.josm.plugins.ods.matching.update;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.BuildingEntityType;
import org.openstreetmap.josm.plugins.ods.matching.Match;

/**
 * The updater updates objects in the Osm layer with new data from the OpenData layer.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdsUpdater {
    private OdsModule module;
    private Map<EntityType<? extends Entity>, EntityUpdater<? extends Entity>> entityUpdaters = new HashMap<>();
    
    public OdsUpdater(OdsModule module) {
        super();
        this.module = module;
        // TODO improve configuration and generics for the entity updaters
        this.entityUpdaters.put(BuildingEntityType.getInstance(), new BuildingUpdater(module));
    }

    public void doUpdate(Collection<OsmPrimitive> primitives) {
        LayerManager layerManager = module.getOpenDataLayerManager();
        for (OsmPrimitive primitive : primitives) {
            Entity entity = layerManager.getEntity(primitive);
            if (entity != null && entity.getMatch() != null) {
                Match<?> match = entity.getMatch();
                EntityType<?> entityType = match.getEntityType();
                EntityUpdater<? extends Entity> updater = entityUpdaters.get(entityType);
                if (updater != null) {
                    updater.update(entity.getMatch());
                    entity.getMatch().analyze();
                    entity.getMatch().updateMatchTags();
                }
            }
        }
    }
}