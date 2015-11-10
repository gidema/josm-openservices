package org.openstreetmap.josm.plugins.ods.entities.enrichment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.AddressNodeGroup;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Point;

/**
 * This enricher finds overlapping nodes in the data and distibutes them, so
 * they are no longer overlapping. The MatchAddressToBuildingTask must run
 * before this class, so when can distribute over the line pointing to the
 * center of the building.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 * 
 */
public class DistributeAddressNodes implements Consumer<Building> {
    private GeoUtil geoUtil;

    public DistributeAddressNodes(GeoUtil geoUtil) {
        super();
        this.geoUtil = geoUtil;
    }

    @Override
    public void accept(Building building) {
        for (AddressNodeGroup group : buildGroups(building).values()) {
            if (group.getAddressNodes().size() > 1) {
                distribute(group, false);
            }
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
        for (AddressNode node : nodes) {
            Point point = geoUtil.toPoint(new Coordinate(x, y));
            node.setGeometry(point);
            x = x + dx;
            y = y + dy;
        }
    }
}
