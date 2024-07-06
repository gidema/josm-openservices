package org.openstreetmap.josm.plugins.ods.mapping.update;

import static org.openstreetmap.josm.plugins.ods.mapping.UpdateStatus.Addition;

import java.util.Collection;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.tools.I18n;

public class AdditionsCommand extends SequenceCommand {
    private final OdsContext context;
    
    public AdditionsCommand(OdsContext context, Collection<Command> sequenz) {
        super(I18n.tr("ODS Additions"), sequenz);
        this.context = context;
    }

    @Override
    public void undoCommand() {
        super.undoCommand();
        var osmLayerManager = context.getComponent(OsmLayerManager.class);
        this.getParticipatingPrimitives().stream().filter(p -> p.hasKeys()).forEach(p -> {
            OsmEntity osmEntity = osmLayerManager.getEntity(p);
            if (osmEntity != null) {
                var mapping = osmEntity.getMapping();
                mapping.remove(osmEntity);
                mapping.getOpenDataEntities().forEach(odEntity -> {
                    odEntity.setUpdateStatus(Addition);
                    odEntity.getMapping().refreshUpdateTags();
                });
            }
        });
    }
}
