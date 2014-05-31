package org.openstreetmap.josm.plugins.ods.builtenvironment;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.openstreetmap.josm.plugins.ods.DataLayer;
import org.openstreetmap.josm.plugins.ods.analysis.Analyzer;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Point;

/**
 * This analyzer finds overlapping nodes in the data and distibutes them, so
 * they are no longer overlapping. The AddressToBuildingMatcher analyzer must
 * run before this class, so when can distribute over the line pointing to the
 * center of the building.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 * 
 */
public class AddressNodeDistributor implements Analyzer {
    private static GeoUtil geoUtil = GeoUtil.getInstance();
    private static double SQRT2 = Math.sqrt(2.0);
    private static AddressNodeComparator addressNodeComparator =
        new AddressNodeComparator();
    
    private double DISTANCE = 2e-7; // Distance between nodes

    public AddressNodeDistributor() {
        super();
    }

    @Override
    public void analyze(DataLayer dataLayer, EntitySet newEntities) {
        EntitySet entities = newEntities;
        if (entities == null) {
            entities = dataLayer.getEntitySet();
        }
        BuiltEnvironment environment = new BuiltEnvironment(entities);
        Iterator<Building> it = environment.getBuildings().iterator();
        while (it.hasNext()) {
            Building building = it.next();
            for (AddressNodeGroup group : buildGroups(building).values()) {
                if (group.getAddressNodes().size() > 1) {
                    distribute(group);
                }
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
        Iterator<? extends AddressNode> it = building.getAddressNodes().iterator();
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

    private void distribute(AddressNodeGroup group) {
        List<AddressNode> nodes = group.getAddressNodes();
        Collections.sort(nodes, addressNodeComparator);
        Coordinate start = group.getGeometry().getCoordinate();
        // center of the building
        Coordinate  center = group.getBuilding().getGeometry().getCentroid().getCoordinate();
        double dx;
        double dy;
        if (start.equals(center)) {
            // if the nodes are at the center of the building, use an angle of 45 degrees
            dx = SQRT2 * DISTANCE;
            dy = SQRT2 * DISTANCE;
        }
        else {
            double angle = new LineSegment(start, center).angle();
            dx = Math.cos(angle) * DISTANCE;
            dy = Math.sin(angle) * DISTANCE;
        }
        double x = group.getGeometry().getX();
        double y = group.getGeometry().getY();
//        List<Command> cmds = new LinkedList<>();
        for (AddressNode node : nodes) {
            Point point = geoUtil.toPoint(new Coordinate(x, y));
            node.setGeometry(point);
//            Command cmd = node.updateGeometry(point);            
//            if (cmd != null) {
//                cmds.add(cmd);
//            }
            x = x + dx;
            y = y + dy;
        }
//        if (!nodes.isEmpty()) {
//            final SequenceCommand sequenceCommand = new SequenceCommand(
//                I18n.tr("Distribute {0} address nodes.", cmds.size()), cmds);
//            Main.main.undoRedo.add(sequenceCommand);
//        }
    }
    
    static class AddressNodeComparator implements Comparator<AddressNode> {

        @Override
        public int compare(AddressNode an1, AddressNode an2) {
            Address a1 = an1.getAddress();
            Address a2 = an2.getAddress();
            int result = ObjectUtils.compare(a1.getCityName(), a2.getCityName());
            if (result == 0) {
                result = ObjectUtils.compare(a1.getPostcode(), a2.getPostcode());
            };
            if (result == 0) {
                result = ObjectUtils.compare(a1.getStreetName(), a2.getStreetName());
            };
            if (result == 0) {
                result = ObjectUtils.compare(a1.getHouseNumber(), a2.getHouseNumber());
            };
            return result;
        }
    }
}
