package org.openstreetmap.josm.plugins.ods.osm;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;

/**
 * Node distance within.
 * <p>There are several methods to check if two nodes are within a certain distance of each other.
 * Each method has is own tradeoffs with respect to speed and accuracy.</p>
 * 
 * <p>The comparison can be based on EastWest or LatLon coordinates.<br/>
 * The coordinates can be compared using a square/rectangle or a circle/ellipse around a coordinate<br/>
 * When using LatLon coordinates, the X offset in degrees can be the same as the Y offset, or the X offset
 * could be calculated from the latitude, thus making sure that the offset in meters is
 * approximately the same in both directions<br/>
 * When recalculating the X-offset, the offset could be calculated once with a reasonable
 * latitude for all nodes or, for more accuracy, it could be calculated for each individual node.</p>
 *  
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface NodeDWithin {
    /**
     * Check if node2 is within the tolerated distance around node1.
     * @param node1
     * @param node2
     * @return
     */
    public boolean check(Node node1, Node node2);
    
    /**
     * Check if the distance between node n and the line between node1 and node2 is within
     *  the tolerated distance.
     *  
     * @param node1
     * @param node2
     * @return
     */
    public boolean check(Node n, Node node1, Node node2);
    
    /**
     * Find the nearest node in a dataset that is within the tolerated distance from a node
     * 
     * @param dataset The dataset to search
     * @param node The node to search for
     * 
     * @return The nearest node in the dataset to the given node that doesn't equal the given node
     *     Or null if no (other) node is within the given distance
     */
    public Node findNode(DataSet dataSet, Node node);
}
