package org.openstreetmap.josm.plugins.ods.test.util;

import java.util.List;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;

public class PrimitiveFactory {
    public static Node creatNode(Double lat, Double lon) {
        LatLon latLon = new LatLon(lat, lon);
        return new Node(latLon);
    }
    
    public static Way createWay(List<Node> nodes) {
        Way way = new Way();
        way.setNodes(nodes);
        return way;
    }
    
    public static Way createWay(Node ... nodes) {
        Way way = new Way();
        for (Node node : nodes) {
            way.addNode(node);
        }
        return way;
    }

}
