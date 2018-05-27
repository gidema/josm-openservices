package org.openstreetmap.josm.plugins.ods.matching.update;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.matching.Match;

/**
 * The updater updates objects in the Osm layer with new data from the OpenData layer.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdsUpdater {
    private final OdsModule module;
    private final List<EntityUpdater> entityUpdaters = new LinkedList<>();

    public OdsUpdater(OdsModule module) {
        super();
        this.module = module;
        // TODO improve configuration and generics for the entity updaters
        this.entityUpdaters.add(new BuildingUpdater(module));
    }

    public void doUpdate(Collection<OsmPrimitive> primitives) {
        OdLayerManager layerManager = module.getOpenDataLayerManager();
        List<Match<?, ?>> updateableMatches = new LinkedList<>();
        for (OsmPrimitive primitive : primitives) {
            OdEntity entity = layerManager.getEntity(primitive);
            if (entity != null && entity.getMatch() != null
                    && entity.getMatch().isSimple()) {
                updateableMatches.add(entity.getMatch());
            }
        }
        for (EntityUpdater updater : entityUpdaters) {
            updater.update(updateableMatches);
        }
        for (Match<?, ?> match : updateableMatches) {
            match.analyze();
            match.updateMatchTags();
        }
    }
}