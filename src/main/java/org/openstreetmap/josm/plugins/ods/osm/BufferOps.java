package org.openstreetmap.josm.plugins.ods.osm;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;

/**
 * Buffer operations.
 * 
 * <p>There are several methods to check if objects are within a certain distance of each other.
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
 * @author Gertjan Idema
 *
 */
public interface BufferOps {
    
    
    /**
     * Get the buffer size
     * 
     * @return
     */
    public double getSize();
    /**
     * Check if node2 is within the buffer around node1.
     * @param node1
     * @param node2
     * @return
     */
    public boolean check(Node node1, Node node2);
    
    /**
     * Check if node n is within the buffer around line between node1 and node2
     *  
     * @param node1
     * @param node2
     * @return
     */
    public boolean check(Node n, Node node1, Node node2);
    
    /**
     * Find the nearest node in a dataset that is within the buffer around a given node
     * This method shouldn't return deleted nodes.
     * 
     * @param dataset The dataset to search
     * @param node The node to search for
     * 
     * @return The nearest node in the dataset to the given node that doesn't equal the given node
     *     Or null if no (other) node is within the buffer zone.
     */
    public Node findNode(DataSet dataSet, Node node);

    /**
     * Create a buffer boundingbox around a segment determined by 2 nodes
     * 
     * @param n1
     * @param n2
     * @return
     */
    public BBox getBBox(Node n1, Node n2);

    /**
     * Create a buffer boundingbox around an other boundingbox.
     * 
     * @param way
     * @return
     */
    public BBox getBBox(BBox bbox);
    
    /**
     * Create a buffer boundingbox around an osm primitive.
     * 
     * @param way
     * @return
     */
    public BBox getBBox(OsmPrimitive p);

}
