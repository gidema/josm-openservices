package org.openstreetmap.josm.plugins.ods.osm;

import org.openstreetmap.josm.data.osm.Node;

public class SegmentMatcher {
    private static double HALF_PI = Math.PI / 2;
    private NodeDWithin dWithin;
    private boolean reversed;
    private MatchType startMatch;
    private MatchType endMatch;
    
    public SegmentMatcher(NodeDWithin dWithin) {
        super();
        this.dWithin = dWithin;
    }

    /**
     * Check if the first segments of the provided iterator are overlap with respect
     * to the tolerance of this Segment matcher.
     * The algorithm is optimized for the cases where there is no match as this will
     * be the majority of the cases; 
     * 
     * @param it1
     * @param it2
     * @return true if the segments match
     */
    public boolean match(NodeIterator it1, NodeIterator it2) {
//        if (!matchLine(it1, it2)) {
//            return false;
//        }
        Node start1 = it1.peek();
        Node start2 = it2.peek();
        Node end2 = it2.peekNext();
        Node end1 = it1.peekNext();
        reversed = (Math.abs(it1.angle(it2)) > HALF_PI);
        if (!reversed && matchPointToPoint(start1, start2) ||
             (reversed && matchPointToPoint(start1, end2))) {
            startMatch = MatchType.NodeToNode;
        }
        else if (matchPointToSegment(start1, start2, end2)) {
            startMatch = MatchType.NodeToSegment;
        }
        else if ((!reversed && matchPointToSegment(start2, start1, end1)) ||
                (reversed && matchPointToSegment(end2, start1, end1))) {
            startMatch = MatchType.SegmentToNode;
        }
        else {
            startMatch = MatchType.NoMatch;
            return false;
        }
        if (!reversed && matchPointToPoint(end1, end2) ||
                (reversed && matchPointToPoint(end1, start2))) {
               endMatch = MatchType.NodeToNode;
        } else if (matchPointToSegment(end1, start2, end2)) {
                endMatch = MatchType.NodeToSegment;
        }
        else if ((!reversed && matchPointToSegment(end2, start1, end1)) ||
                (reversed && matchPointToSegment(start2, start1, end1))) {
            endMatch = MatchType.SegmentToNode;
        }
        else {
            endMatch = MatchType.NoMatch;
            return false;
        }
        return true;

    }

    public boolean matchLine(NodeIterator it1, NodeIterator it2) {
        Node start1 = it1.peek();
        Node end1 = it1.peekNext();
        Node start2 = it2.peek();
        Node end2 = it2.peekNext();
        return dWithin.check(start2, start1, end1) &&
            dWithin.check(end2, start1, end1);
    }
    
    private boolean matchPointToPoint(Node n1, Node n2) {
        return n1.equals(n2) || dWithin.check(n1, n2);
    }

    private boolean matchPointToSegment(Node n, Node n1, Node n2) {
        return (!matchPointToPoint(n1, n) && !matchPointToPoint(n2, n)
             && dWithin.check(n, n1, n2)); 
    }

    
//    /**
//     * Calculate the distance from node n0 to the line through n1 and n2
//     * @param n1
//     * @param n2
//     * @param n0
//     * @return
//     */
//    public double distanceToLine(Node n1, Node n2, Node n0) {
//        double x0 = en0.getX();
//        double y0 = en0.getY();
//        double x1 = en1.getX();
//        double y1 = en1.getY();
//        double x2 = en2.getX();
//        double y2 = en2.getY();
//        double dx = x2 - x1;
//        double dy = y2 - y1;
//        double distance = Math.abs(dy * x0 - dx * y0 - x1 * y2 + x2 * y1) /
//                Math.sqrt(dx * dx + dy * dy);
//        return distance;
//    }

    public boolean isreversed() {
        return reversed;
    }

    public MatchType getStartMatch() {
        return startMatch;
    }
    
    public MatchType getEndMatch() {
       return endMatch;
    }

    enum MatchType {
        NoMatch,
        NodeToNode,
        NodeToSegment,
        SegmentToNode
    }
}
