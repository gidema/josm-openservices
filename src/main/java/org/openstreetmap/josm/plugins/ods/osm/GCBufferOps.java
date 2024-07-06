package org.openstreetmap.josm.plugins.ods.osm;


import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;

import static java.lang.Math.sqrt;

/**
 * BufferOps implementation that uses a Great Circle approximation to calculate the
 * distances.
 * The Great Circle approximation assumes that the earth is a perfect sphere.
 * 
 * When working with LatLon coordinates, a square buffer in real life doesn't translate to a square
 * buffer in LatLon coordinates. The longitude get smaller as we move away from the equator.
 * 
 * With the great circle approximation, we can calculate a fixed buffer height (dLat) in the Latitude (y) direction.
 * The buffer width (dx) depends on the latitude. For small areas is can be assumed to be a static value.  
 * 
 * @author Gertjan Idema
 *
 */
public class GCBufferOps implements BufferOps {
    // private static double DEG2RAD = Math.PI * 2 / 360; // Degrees to radians
    private static double EARTH_CIRCUMFERENCE = 4e7;

    private final double bufferSize;
    private final double dLat; // buffer height in degrees
    private final double maxDLon; // Fixed buffer width in degrees
    private final boolean fixedLatitude; // The latitude and thus maxDx are fixed
    
    /**
     * Constructor using a given bufferSize in meters.
     * From the bufferSize in meters, a buffer height dLat in degrees is calculated to compare the latitudes.
     * 
     * The dLon buffer width will be calculated for each comparison, based on the
     * latitude
     * 
     * @param bufferSize
     */
    public GCBufferOps(double bufferSize) {
        this.bufferSize = bufferSize;
        dLat = 360 * bufferSize / EARTH_CIRCUMFERENCE;
        maxDLon = 0; // The value is never used.
        fixedLatitude = false;
    }

    /**
     * Constructor using a given bufferSize in meters and a fixed latitude.
     *
     * From the bufferSize in meters, a dLat in degrees is calculated to compare the latitudes
     * 
     * The dx tolerance in degrees is calculated from the dLat tolerance, using the fixed latitude.
     * 
     * @param bufferSize
     */
    public GCBufferOps(double bufferSize, double latitude) {
        this.bufferSize = bufferSize;
        dLat = 360 * bufferSize / EARTH_CIRCUMFERENCE;
        maxDLon = getDLon(latitude);
        fixedLatitude = true;
    }

    @Override
    public double getSize() {
        return bufferSize;
    }

    @Override
    public boolean check(Node node1, Node node2) {
        if (node1.equals(node2)) {
            return true;
        }
        // First check the distance in the y (latitude) direction, because
        // the calculation is cheaper;
        if (Math.abs(node1.getCoor().lat() - node2.getCoor().lat()) > dLat) {
            return false;
        }
        double dLon;
        if (fixedLatitude) {
            dLon = maxDLon;
        }
        else {
            dLon = getDLon(node1.getCoor().lat());
        }
        return Math.abs(node1.getCoor().lon() - node2.getCoor().lon()) <= dLon;
    }

    
    @Override
    public boolean check(Node n, Node node1, Node node2) {
        
        if (check(n, node1) || check(n, node2)) return true;
        
        LatLon ll1 = node1.getCoor();
        LatLon ll2 = node2.getCoor();
        LatLon ll = n.getCoor();

        if (Math.max(ll1.lat(), ll2.lat()) + dLat < ll.lat()) return false;
        if (Math.min(ll1.lat(), ll2.lat()) - dLat > ll.lat()) return false;
        double maxDx = getDLon(ll.lat());
        if (Math.max(ll1.lon(), ll2.lon()) + maxDx < ll.lon()) return false;
        if (Math.min(ll1.lon(), ll2.lon()) - maxDx > ll.lon()) return false;

        double ldx = ll2.lon() - ll1.lon(); // dx for the line segment
        double ldy = ll2.lat() - ll1.lat(); // dLat for the line segment

        // Segment with 0 length
        if (ldx == 0 && ldy == 0) return false;
        if (ldx == 0) {
            // Special case: vertical line. Because we already checked the bounding box we can safely return true.
            return true;
        }
        
        // represent the line with y = mx + k
        double m = ldy/ldx;
        double k = ll1.lat() - m * ll1.lon();
        
        // get the projected X and Y values.
        double projectedX = (ll.lon() + m * ll.lat() -m * k) / (m * m + 1);
        double projectedY = m * projectedX + k;
        // If the distance to the original y
        // value is larger than dLat we can return false;
        if (Math.abs(projectedY - ll.lat()) > dLat) return false;
        return Math.abs(projectedX - ll.lon()) <= maxDx;
    }

    /**
     * Get the buffer width (dLon) value for the given latitude
     * 
     * @param lat
     * @return
     */
    private double getDLon(double lat) {
        if (fixedLatitude) {
            return maxDLon;
        }
        return dLat * Math.cos(Math.toRadians(lat));
    }

    @Override
    public Node findNode(DataSet dataSet, Node node) {
        BBox bbox = getBBox(node);
        Node found = null;
        // Keep track of the minimal squared distance to determine the nearest node
        double minDSquared = Double.POSITIVE_INFINITY;
        for (Node candidate : dataSet.searchNodes(bbox)) {
            if (!candidate.equals(node) && !candidate.isDeleted() && check(candidate, node)) {
                double dSquared = sqrt(node.lat() - candidate.lat()) + sqrt(node.lon() - candidate.lon());
                if (dSquared < minDSquared) {
                    found = candidate;
                    minDSquared = dSquared;
                }
            }
        }
        return found;
    }

    @Override
    public BBox getBBox(Node n1, Node n2) {
        double dLon = getDLon(n1.getCoor().lat());
        double minLon = Math.min(n1.lon(), n2.lon());
        double maxLon = Math.max(n1.lon(), n2.lon());
        double minLat = Math.min(n1.lat(), n2.lat());
        double maxLat = Math.max(n1.lat(), n2.lat());
        return getBBox(minLon, maxLon, minLat, maxLat, dLon);
    }

    @Override
    public BBox getBBox(OsmPrimitive p) {
        return getBBox(p.getBBox());
    }
    
    @Override
    public BBox getBBox(BBox bbox) {
        double dLon = getDLon(bbox.getCenter().lat());
        return getBBox(bbox.getMinLon(), bbox.getMaxLon(), bbox.getMinLat(), bbox.getMaxLat(), dLon);
    }
    
    private BBox getBBox(double minLon, double maxLon, double minLat, double maxLat, double dLon) {
        return new BBox(minLon - dLon, maxLon + dLon, minLat - dLat, maxLat + dLat);
    }
}
