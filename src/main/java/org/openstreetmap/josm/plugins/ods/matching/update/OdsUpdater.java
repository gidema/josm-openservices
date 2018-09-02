package org.openstreetmap.josm.plugins.ods.matching.update;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.Deviation;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.tools.I18n;

/**
 * The updater updates objects in the Osm layer with new data from the OpenData layer.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdsUpdater {
    private final OsmLayerManager layerManager;

    public OdsUpdater(OsmLayerManager layerManager) {
        super();
        this.layerManager = layerManager;
    }

    public void doUpdate(Collection<OsmPrimitive> primitives) {
        List<OsmEntity> updateableEntities = new LinkedList<>();
        for (OsmPrimitive primitive : primitives) {
            OsmEntity entity = layerManager.getEntity(primitive);
            if (entity != null && entity.getDeviations() != null) {
                updateableEntities.add(entity);
            }
        }
        List<Command> commands = new LinkedList<>();
        for (OsmEntity entity : updateableEntities) {
            Iterator<Deviation> it = entity.getDeviations().iterator();
            while (it.hasNext()) {
                Deviation deviation = it.next();
                if (deviation.isFixable()) {
                    commands.add(deviation.getFix());
                    deviation.clearOdsTags();
                    it.remove();
                }
            }
        }
        UndoRedoHandler.getInstance().add(new SequenceCommand(I18n.tr("Update properties"), commands));
    }
}