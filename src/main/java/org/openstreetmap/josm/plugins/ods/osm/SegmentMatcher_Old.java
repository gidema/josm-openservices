package org.openstreetmap.josm.plugins.ods.osm;

import org.openstreetmap.josm.data.coor.EastNorth;

@Deprecated
public class SegmentMatcher_Old {
    private static double HALF_PI = Math.PI / 2;
    private double tolerance;
    private boolean reversed;
    private MatchType startMatch;
    private MatchType endMatch;
    
    public SegmentMatcher_Old(double tolerance) {
        super();
        this.tolerance = tolerance;
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
        if (!matchLine(it1, it2)) {
            return false;
        }
        EastNorth start1 = it1.peek().getEastNorth();
        EastNorth start2 = it2.peek().getEastNorth();
        EastNorth end2 = it2.peekNext().getEastNorth();
        EastNorth end1 = it1.peekNext().getEastNorth();
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
        EastNorth start1 = it1.peek().getEastNorth();
        EastNorth end1 = it1.peekNext().getEastNorth();
        EastNorth start2 = it2.peek().getEastNorth();
        EastNorth end2 = it2.peekNext().getEastNorth();
        return distanceToLine(start1, end1, start2) <= tolerance &&
            distanceToLine(start1, end1, end2) <= tolerance;    
    }
    
    private boolean matchPointToPoint(EastNorth en1, EastNorth en2) {
        return en1.equals(en2) || en1.distance(en2) <= tolerance;
    }

    private boolean matchPointToSegment(EastNorth en, EastNorth en1, EastNorth en2) {
        return (!matchPointToPoint(en1, en) && !matchPointToPoint(en2, en)
             && Util.distancePointLine(en, en1, en2) <= tolerance); 
    }

    
    /**
     * Calculate the distance from point en0 to the line through en1 and en2
     * @param en1
     * @param en2
     * @param en0
     * @return
     */
    public double distanceToLine(EastNorth en1, EastNorth en2, EastNorth en0) {
        double x0 = en0.getX();
        double y0 = en0.getY();
        double x1 = en1.getX();
        double y1 = en1.getY();
        double x2 = en2.getX();
        double y2 = en2.getY();
        double dx = x2 - x1;
        double dy = y2 - y1;
        double distance = Math.abs(dy * x0 - dx * y0 - x1 * y2 + x2 * y1) /
                Math.sqrt(dx * dx + dy * dy);
        return distance;
    }

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
