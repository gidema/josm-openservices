package org.openstreetmap.josm.plugins.ods.builtenvironment.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.command.ChangeCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.DeleteCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.gui.PleaseWaitRunnable;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.gui.OdsAction;
import org.openstreetmap.josm.tools.I18n;
import org.xml.sax.SAXException;

public class RemoveAssociatedStreetsAction extends OdsAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public RemoveAssociatedStreetsAction(OdsContext context) {
        super(context, I18n.tr("Remove associated streets"), I18n
                .tr("Remove associated street relations."));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (getContext().getComponent(OsmLayerManager.class) == null) {
            new Notification(I18n.tr("The OSM datalayer is missing")).show();
            return;
        }
        MainApplication.worker.execute(new Task());
    }

    class Task extends PleaseWaitRunnable {

        public Task() {
            super(I18n.tr("Please wait"));
        }

        @Override
        protected void cancel() {
            // TODO Auto-generated method stub

        }

        @Override
        protected void realRun() throws SAXException, IOException,
        OsmTransferException {
            OsmDataLayer dataLayer = getContext().getComponent(OsmLayerManager.class)
                    .getOsmDataLayer();
            for (Relation relation : dataLayer.getDataSet().getRelations()) {
                if ("associatedStreet".equals(relation.get("type"))) {
                    process(relation);
                }
            }
            MainApplication.getMap().repaint();
        }

        private void process(Relation oldAssociatedStreet) {
            // Create an iterator for the members of the associatedStreet
            // relation
            @SuppressWarnings({ "rawtypes", "unchecked" })
            List<RelationMember> members = new LinkedList(
                    oldAssociatedStreet.getMembers());
            Iterator<RelationMember> it = members.iterator();
            while (it.hasNext()) {
                RelationMember member = it.next();
                OsmPrimitive primitive = null;
                switch (member.getType()) {
                case NODE:
                    primitive = member.getNode();
                    break;
                case WAY:
                case CLOSEDWAY:
                    primitive = member.getWay();
                    break;
                case RELATION:
                case MULTIPOLYGON:
                    primitive = member.getRelation();
                    break;
                }
                if (primitive == null) {
                    continue;
                }
                // Remove any complete relationMember
                if (!primitive.isIncomplete()) {
                    it.remove();
                }
            }
            Command cmd;
            if (members.size() != oldAssociatedStreet.getMembersCount()) {
                if (members.isEmpty()) {
                    // The associated street relation has no members left, so we
                    // can remove it
                    cmd = new DeleteCommand(oldAssociatedStreet);
                } else {
                    Relation newAssociatedStreet = new Relation(
                            oldAssociatedStreet);
                    newAssociatedStreet.setMembers(members);
                    cmd = new ChangeCommand(oldAssociatedStreet,
                            newAssociatedStreet);
                }
                UndoRedoHandler.getInstance().add(cmd);
            }
        }

        @Override
        protected void finish() {
            // TODO Auto-generated method stub

        }

    }
}
