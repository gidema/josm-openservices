package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.DataLayer;
import org.openstreetmap.josm.plugins.ods.analysis.Analyzer;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Point;

/**
 * This analyzer finds overlapping nodes in the data and distibutes them,
 * so they are no longer overlapping.
 * The AddressToBuildingMatcher analyzer must run before this class, so when
 * can distribute over the line pointing to the center of the building. 
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class AddressNodeDistributor implements Analyzer {
    private static GeoUtil geoUtil = GeoUtil.getInstance();
    private Map<Point, AddressNodeGroup> addressNodeGroups = new HashMap<>();

    @Override
    public void analyze(DataLayer dataLayer, EntitySet newEntities) {
        buildGroups(newEntities);
        distribute();
    }
    
    /**
     * Analyze all new address nodes and group them by Geometry (Point) 
     * 
     * @param newEntities
     */
    private void buildGroups(EntitySet newEntities) {
        BuiltEnvironment bes = new BuiltEnvironment(newEntities);
        Iterator<AddressNode> it = bes.getAddresses().iterator();
        while (it.hasNext()) {
            AddressNode addressNode = it.next();
            AddressNodeGroup group = addressNodeGroups.get(addressNode.getGeometry());
            if (group == null) {
                group = new AddressNodeGroup(addressNode);
                addressNodeGroups.put(addressNode.getGeometry(), group);
            }
            else {
                group.addAddressNode(addressNode);
            }
        }
    }
    
    private void distribute() {
        Iterator<AddressNodeGroup> it = addressNodeGroups.values().iterator();
        while (it.hasNext()) {
            AddressNodeGroup group = it.next();
            List<AddressNode> nodes = group.getAddressNodes();
            if (nodes.size() == 1) {
                continue;
            }
            Collections.sort(nodes);
            Point center = group.getBuilding().getGeometry().getCentroid();
            LineSegment ls = new LineSegment(group.getGeometry().getCoordinate(), center.getCoordinate());
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
}
