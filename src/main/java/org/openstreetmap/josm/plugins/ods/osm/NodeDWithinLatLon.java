package org.openstreetmap.josm.plugins.ods.osm;


import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import static java.lang.Math.sqrt;

/**
 * NodeDWithin implementation that uses LatLon coordinates to calculate the
 * distances.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class NodeDWithinLatLon implements NodeDWithin {
    // private static double DEG2RAD = Math.PI * 2 / 360; // Degrees to radians
    private static double EARTH_CIRCUMFERENCE = 4e7;

    private final double maxDy; // Maximum delta y in degrees
    private final double maxDxFixed; // Fixed maximum delta x in degrees
    private final boolean fixedLatitude; // The latitude and thus maxDx are fixed
    
    /**
     * Constructor using a given tolerance in meters.
     * The tolerance in meters is calculated to a dy tolerance in degrees
     * to compare the latitudes.
     * The dx tolerance will be calculated for each comparison, based on the
     * latitude of the first Node;
     * 
     * @param tolerance
     */
    public NodeDWithinLatLon(double tolerance) {
        maxDy = 360 * tolerance / EARTH_CIRCUMFERENCE;
        maxDxFixed = 0; // The value is never used.
        fixedLatitude = false;
    }

    /**
     * Constructor using a given tolerance in meters and a fixed latitude.
     *
     * The tolerance in meters is converted to a dy tolerance in degrees
     * to compare the latitudes.
     * The dx tolerance in degrees is calculated from the dy tolerance, using the fixed latitude.
     * 
     * @param tolerance
     */
    public NodeDWithinLatLon(double tolerance, double latitude) {
        maxDy = 360 * tolerance / EARTH_CIRCUMFERENCE;
        maxDxFixed = getMaxDx(latitude);
        fixedLatitude = true;
    }

    @Override
    public boolean check(Node node1, Node node2) {
        if (node1.equals(node2)) {
            return true;
        }
        // First check the distance in the y (latitude) direction, because
        // the calculation is cheaper;
        if (Math.abs(node1.getCoor().lat() - node2.getCoor().lat()) > maxDy) {
            return false;
        }
        double maxDx;
        if (fixedLatitude) {
            maxDx = maxDxFixed;
        }
        else {
            maxDx = getMaxDx(node1.getCoor().lat());
        }
        return Math.abs(node1.getCoor().lon() - node2.getCoor().lon()) <= maxDx;
    }

    
    @Override
    public boolean check(Node n, Node node1, Node node2) {
        
        if (check(n, node1) || check(n, node2)) return true;
        
        LatLon ll1 = node1.getCoor();
        LatLon ll2 = node2.getCoor();
        LatLon ll = n.getCoor();

        if (Math.max(ll1.lat(), ll2.lat()) + maxDy < ll.lat()) return false;
        if (Math.min(ll1.lat(), ll2.lat()) - maxDy > ll.lat()) return false;
        double maxDx = getMaxDx(ll.lat());
        if (Math.max(ll1.lon(), ll2.lon()) + maxDx < ll.lon()) return false;
        if (Math.min(ll1.lon(), ll2.lon()) - maxDx > ll.lon()) return false;

        double ldx = ll2.lon() - ll1.lon(); // dx for the line segment
        double ldy = ll2.lat() - ll1.lat(); // dy for the line segment

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
        // value is larger than maxDy we can return false;
        if (Math.abs(projectedY - ll.lat()) > maxDy) return false;
        return Math.abs(projectedX - ll.lon()) <= maxDx;
    }

    /**
     * Calculate the maximal dx value for the given latitude
     * 
     * @param lat
     * @return
     */
    private double getMaxDx(double lat) {
        return maxDy * Math.cos(Math.toRadians(lat));
    }

    @Override
    public Node findNode(DataSet dataSet, Node node) {
        double maxDx;
        if (fixedLatitude) {
            maxDx = maxDxFixed;
        }
        else {
            maxDx = getMaxDx(node.getCoor().lat());
        }
        double minX = node.lon() - maxDx;
        double maxX = node.lon() + maxDx;
        double minY = node.lat() - maxDy;
        double maxY = node.lat() + maxDy;
        BBox bbox = new BBox(minX, minY, maxX, maxY);
        bbox.addPrimitive(node, maxDy);
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
}
