package org.openstreetmap.josm.plugins.ods.mapping.update;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntityBuilders;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;
import org.openstreetmap.josm.plugins.ods.mapping.OdsMappingJob;
import org.openstreetmap.josm.plugins.ods.update.AdditionHandler;
import org.openstreetmap.josm.plugins.ods.update.DeletionHandler;
import org.openstreetmap.josm.plugins.ods.update.ModificationHandler;

/**
 * The updater updates objects in the Osm layer with new data from the OpenData layer.
 * Only objects selected by the user are updated.
 * To prevent alignment issues like snapping to obsolete nodes, the updates are performed 
 * in a specific order:
 * 1. Remove deleted object
 * 2. Modify geometry and attributes of existing objects
 * 3. Add new objects 
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdsUpdater {
    private final OdsContext context;
    private List<Mapping<?, ?>> deletions = new LinkedList<>();
    private List<Mapping<?, ?>> modifications = new LinkedList<>();
    private List<Mapping<?, ?>> additions = new LinkedList<>();

    public OdsUpdater(OdsContext context) {
        super();
        this.context = context;
    }

    public void doUpdate(Collection<OsmPrimitive> primitives) {
        collectTasks(primitives);
        handleDeletions();
        handleModifications();
        handleAdditions();
        refreshMappings();
    }
    
    private void collectTasks(Collection<OsmPrimitive> primitives) {
        OdLayerManager layerManager = context.getComponent(OdLayerManager.class);
        for (OsmPrimitive primitive : primitives) {
            OdEntity entity = layerManager.getEntity(primitive);
            if (entity != null) {
                var mapping = entity.getMapping();
                switch (entity.getUpdateTaskType()) {
                case ADD:
                    additions.add(mapping);
                    break;
                case DELETE:
                    deletions.add(mapping);
                    break;
                case MODIFY:
                    modifications.add(mapping);
                    break;
                default:
                    break;
                }
            }
        }
    }
    
    private void handleDeletions() {
        DeletionHandler deletionHandler = context.getComponent(DeletionHandler.class);
        if (deletionHandler != null && !deletions.isEmpty()) {
            deletionHandler.handle(deletions);
            List<Command> cmds = deletionHandler.getCommands();
            if (cmds.isEmpty()) return;
            UndoRedoHandler.getInstance().add(new SequenceCommand("Deleted BAGobjects" , cmds));
        }
    }
    
    private void handleModifications() {
        ModificationHandler modificationHandler = context.getComponent(ModificationHandler.class);
        if (modificationHandler != null && !modifications.isEmpty()) {
            modificationHandler.handle(modifications);
            List<Command> cmds = modificationHandler.buildCommands();
            if (cmds.isEmpty()) return;
            UndoRedoHandler.getInstance().add(new SequenceCommand("Modified BAG objects" , cmds));
        }
    }
    
    private void handleAdditions() {
        AdditionHandler additionHandler = context.getComponent(AdditionHandler.class);
        if (additionHandler != null && !additions.isEmpty()) {
            additionHandler.handle(additions);
            additionHandler.getCommand().ifPresent(cmd -> {
                UndoRedoHandler.getInstance().add(cmd);
                removeOdsTags(additionHandler.getAddedWays());
                buildOsmEntities(additionHandler.getAddedWays());
                removeOdsTags(additionHandler.getAddedNodes());
                buildOsmEntities(additionHandler.getAddedNodes());
            });
        }
    }
    
    // TODO move this method to the (abstract) additionhandeler
    private static void removeOdsTags(Collection<? extends OsmPrimitive> addedPrimitives) {
        addedPrimitives.forEach(primitive -> {
            primitive.getKeys().keySet().forEach(key -> {
                if (key.startsWith(ODS.KEY.BASE)) {
                    primitive.put(key, null);
                }
            });
        });
    }

    private void buildOsmEntities(Collection<? extends OsmPrimitive> addedPrimitives) {
        OsmEntityBuilders entityBuilders = context.getComponent(OsmEntityBuilders.class);
        for (OsmPrimitive p : addedPrimitives) {
            entityBuilders.forEach(builder -> builder.buildOsmEntity(p));
        }
    }
    
    private void refreshMappings() {
        OdsMappingJob mappingJob = new OdsMappingJob();
        mappingJob.run(context);
        for (Mapping<?, ?> mapping : deletions) {
            mapping.analyze();
            mapping.refreshUpdateTags();
        }
        for (Mapping<?, ?> mapping : modifications) {
            mapping.analyze();
            mapping.refreshUpdateTags();
        }
        for (Mapping<?, ?> mapping : additions) {
            mapping.analyze();
            mapping.refreshUpdateTags();
        }
    }
}