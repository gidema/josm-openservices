package org.openstreetmap.josm.plugins.ods.jts;

import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LinearRing;

/**
 * The LinearRingAligner aligns two linearRings according to a given tolerance.
 * A point in the second ring that is within 'tolerance' distance from a point
 * from the first ring, will get the coordinates of that point.
 * A lineSegment from either of the rings, that is within 'tolerance'
 * distance from a point on the other ring, will be split by adding that
 * point in between the start and end point of that segment.
 * 
 * @author gertjan
 *
 */
@Deprecated
public class LinearRingAligner1 {
    private LinearRing ring1;
    private LinearRing ring2;
    private Double tolerance;
    private boolean ring1Modified = false;
    private boolean ring2Modified = false;

    public LinearRingAligner1(LinearRing ring1, LinearRing ring2,
            Double tolerance) {
        super();
        this.ring1 = ring1;
        this.ring2 = ring2;
        this.tolerance = tolerance;
    }
    
    public void run() {
        SegmentIterator it1 = new SegmentIterator(ring1, false);
        SegmentIterator it2 = new SegmentIterator(ring2, true);
        while (it1.hasNext()) {
            if (search(it1, it2)) {
                if (align(it1, it2)) {
                    trace(it1, it2);
                }
            }
        }
        ring1Modified = it1.isModified();
        ring2Modified = it2.isModified();
        if (ring1Modified) {
            ring1 = it1.getResult();
        }
        if (ring2Modified) {
            ring2 = it2.getResult();
        }
    }
    
    public boolean ring1Modified() {
        return ring1Modified;
    }
    
    public boolean ring2Modified() {
        return ring2Modified;
    }

    public LinearRing getRing1() {
        return ring1;
    }

    public LinearRing getRing2() {
        return ring2;
    }

    /**
     * Synchronize the two iterators, to both point to the first segment
     * that is within 'tolerance' distance from a segment in the other iterator;
     * 
     * @param it1
     * @param it2
     * @return true if a matching pair of segments was found. False otherwise.
     */
    private boolean search(SegmentIterator it1, SegmentIterator it2) {
        while (it1.hasNext()) {
            LineSegment segment1 = it1.next();
            it2.reset(0);
            while (it2.hasNext()) {
                LineSegment segment2 = it2.next();
                if (segment1.distance(segment2) < tolerance) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean align(SegmentIterator it1, SegmentIterator it2) {
        LineSegment ls1 = it1.getCurrent();
        LineSegment ls2 = it2.getCurrent();
        if (ls1.p1.distance(ls2.p1) < tolerance) {
            it2.updateStartPoint(ls1.p1);
            return true;
        }
        if (ls1.p0.distance(ls2.p0) < tolerance) {
            it2.updateStartPoint(ls1.p0);
            return false;
        }
        if (ls1.p0.distance(ls2.p1) < tolerance) {
            it2.updateEndPoint(ls1.p0);
            ls2 = it2.next();
            return false;
        }
        if (ls1.p1.distance(ls2.p0) < tolerance) {
            it2.updateStartPoint(ls1.p1);
            ls1 = it1.next();
            return false;
        }
        if (ls2.distance(ls1.p1) < tolerance) {
            ls2 = it2.snap(ls1.p1, tolerance);
            ls1 = it1.next();
            return true;
        }
        if (ls1.distance(ls2.p1) < tolerance) {
            ls1 = it1.snap(ls2.p1, tolerance);
            ls2 = it2.next();
            return true;
        }
        if (ls2.distance(ls1.p0) < tolerance) {
            ls2 = it2.snap(ls1.p0, tolerance);
            return true;
        }
        if (ls1.distance(ls2.p0) < tolerance) {
            ls1 = it1.snap(ls2.p0, tolerance);
            return true;
        }
        return false;
    }
    
    private void trace(SegmentIterator it1, SegmentIterator it2) {
        LineSegment ls1 = it1.getCurrent();
        LineSegment ls2 = it2.getCurrent();
        it2.reset();
        while (it1.hasNext() && it2.hasNext()) {
            if (ls1.p1.equals(ls2.p1)) {
                // Matching points. Forward both iterators
                ls1 = it1.next();
                ls2 = it2.next();
            }
            else if (ls1.p1.distance(ls2.p1) < tolerance) {
                // Nearby points. Update the coordinate of the end point of the second segment to that of the first
                it2.updateEndPoint(ls1.p1);
                ls1 = it1.next();
                ls2 = it2.next();
            }
            else if (ls1.distance(ls2.p1) < tolerance) {
                ls1 = it1.snap(ls2.p1, tolerance);
                ls1 = it1.getCurrent();
            }
            else if (ls2.distance(ls1.p1) < tolerance) {
                ls2 = it2.snap(ls1.p1, tolerance);
                ls1 = it1.next();
            }
            else {
                it1.next();
                it2.next();
                return;
            }
        }
    }
}
