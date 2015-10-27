package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.AddressNodeGroup;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.opendata.OpenDataBuildingStore;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.tasks.Task;
import org.openstreetmap.josm.tools.I18n;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Point;

/**
 * This analyzer finds overlapping nodes in the data and distibutes them, so
 * they are no longer overlapping. The MatchAddressToBuildingTask must run
 * before this class, so when can distribute over the line pointing to the
 * center of the building.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 * 
 */
public class DistributeAddressNodesTask implements Task {
    private GeoUtil geoUtil;
    private OpenDataBuildingStore buildingStore;

    public DistributeAddressNodesTask(GeoUtil geoUtil,
            OpenDataBuildingStore buildingStore) {
        super();
        this.geoUtil = geoUtil;
        this.buildingStore = buildingStore;
    }

    @Override
    public void run(Context ctx) {
//        EntitySource entitySource = (EntitySource) ctx.get("entitySource");
        for (Building building : buildingStore) {
//            if (building.getEntitySource() == entitySource) {
                for (AddressNodeGroup group : buildGroups(building).values()) {
                    if (group.getAddressNodes().size() > 1) {
                        distribute(group, false);
                    }
                }
//            }
        }
    }

    /**
     * Analyze all new address nodes and group them by Geometry (Point)
     * 
     * @param newEntities
     */
    private Map<Point, AddressNodeGroup> buildGroups(Building building) {
        Map<Point, AddressNodeGroup> groups = new HashMap<>();
        Iterator<AddressNode> it = building.getAddressNodes().iterator();
        while (it.hasNext()) {
            AddressNode addressNode = it.next();
            AddressNodeGroup group = groups.get(addressNode.getGeometry());
            if (group == null) {
                group = new AddressNodeGroup(addressNode);
                groups.put(addressNode.getGeometry(), group);
            } else {
                group.addAddressNode(addressNode);
            }
        }
        return groups;
    }

    private void distribute(AddressNodeGroup group, boolean withUndo) {
        List<AddressNode> nodes = group.getAddressNodes();
        Collections.sort(nodes);
        if (group.getBuilding().getGeometry().isEmpty()) {
            // Happens rarely,
            // for now return to prevent null pointer Exception
            return;
        }
        Point center = group.getBuilding().getGeometry().getCentroid();
        LineSegment ls = new LineSegment(group.getGeometry().getCoordinate(),
                center.getCoordinate());
        double angle = ls.angle();
        double dx = Math.cos(angle) * 2e-7;
        double dy = Math.sin(angle) * 2e-7;
        double x = group.getGeometry().getX();
        double y = group.getGeometry().getY();
        List<Command> cmds = new LinkedList<>();
        for (AddressNode node : nodes) {
            Point point = geoUtil.toPoint(new Coordinate(x, y));
            // if (withUndo) {
            // Command cmd = node.updateGeometry(point);
            // if (cmd != null) {
            // cmds.add(cmd);
            // }
            // }
            // else {
            node.setGeometry(point);
            // }
            x = x + dx;
            y = y + dy;
        }
        if (!cmds.isEmpty()) {
            final SequenceCommand sequenceCommand = new SequenceCommand(
                    I18n.tr("Distribute {0} address nodes.", cmds.size()), cmds);
            Main.main.undoRedo.add(sequenceCommand);
        }
    }
}
