package org.openstreetmap.josm.plugins.ods.osm.update;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.plugins.ods.osm.update.PoolNode.SpecialReferrers;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class NodePool {
    private static double DEG2RAD = Math.PI * 2 / 360; // Degrees to radians
    private static double EARTH_CIRCUMFERENCE = 4e7;
    private double dy;
    
    private Map<Node, PoolNode> osmNodeMapping = new HashMap<>();
    private Map<Node, PoolNode> openDataNodeMapping = new HashMap<>();
    private Quadtree quadTree = new Quadtree();

    public NodePool() {
        this(0.05);
    }
    
    public NodePool(double tolerance) {
        dy = 360 * tolerance / EARTH_CIRCUMFERENCE;
    }
    
    public boolean contains(Node node) {
        return osmNodeMapping.containsKey(node);
    }
    
    public PoolNode addNode(Node node, SpecialReferrers referrers) {
        Envelope envelope = createEnvelope(node, dy);
        PoolNode poolNode = new PoolNode(node, envelope, referrers);
        osmNodeMapping.put(node, poolNode);
        quadTree.insert(poolNode.getEnvelope(), poolNode);
        return poolNode;
    }
    
    public PoolNode getMatchingNode(Node odNode) {
        // Check if this open data node has already been matched
        PoolNode poolNode = openDataNodeMapping.get(odNode);
        if (poolNode != null) return poolNode;
        poolNode = nearestNode(odNode);
        if (poolNode != null) {
            poolNode.setMatched(true);
        }
        return poolNode;
    }
    
    private PoolNode nearestNode(Node node) {
        List<?> searchResult = quadTree.query(createEnvelope(node, 0));
        PoolNode nearestNode = null;
        double shortestDistance = 0;
        for (Object o : searchResult) {
            assert (o instanceof PoolNode);
            PoolNode foundNode = (PoolNode)o;
            if (foundNode.isMatched()) continue;
            double distance = foundNode.distanceTo(node);
            if (nearestNode == null) {
                nearestNode = foundNode;
                shortestDistance = distance;
            }
            else {
                if (distance < shortestDistance) {
                    nearestNode = foundNode;
                    shortestDistance = distance;
                }
            }
        }
        return nearestNode;
    }
    
    public PoolNodeProvider getPoolNodeProvider() {
        return new PoolNodeProvider(osmNodeMapping.values().iterator());
    }
    
    /**
     * Create an envelope around a node with a height of 2x dy degrees.
     * The width of the envelope will be calculate from dy and the latitude in
     * such a way that the envelope will we approximately square.
     * 
     * @param node The node to create the envelope for
     * @param dy Half the height of the requested envelope in degrees
     * @return
     */
    private Envelope createEnvelope(Node node, double dy) {
        double dx = 0;
        double lon = node.getCoor().getX();
        double lat = node.getCoor().getY();
        if (dy != 0) {
            dx = dy / Math.sin(lat * DEG2RAD);
        }
        return new Envelope(lon - dx, lon +dx, lat - dy, lat + dy);
    }

    public class PoolNodeProvider {
        Iterator<PoolNode> it;
        
        PoolNodeProvider(Iterator<PoolNode> iterator) {
            this.it = iterator;
        }
        
        public PoolNode get(Node node) {
            if (!it.hasNext()) {
                return null;
            }
            PoolNode poolNode = it.next();
            while (poolNode.isMatched() && it.hasNext()) {
                poolNode = it.next();
            };
            if (poolNode != null) {
                openDataNodeMapping.put(node, poolNode);
                poolNode.setMatched(true);
            }
            return poolNode;
        }
    }
}
