package org.openstreetmap.josm.plugins.ods.jts;

import com.vividsolutions.jts.geom.Coordinate;
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
public class LinearRingAligner {
    private LinearRing ring1;
    private LinearRing ring2;
    private SegmentIterator it1;
    private SegmentIterator it2;
    private Double tolerance;

    public LinearRingAligner(LinearRing ring1, LinearRing ring2,
            Double tolerance) {
        this.ring1 = ring1;
        this.ring2 = ring2;
        this.tolerance = tolerance;
    }
    
    public void run() {
        it1 = new SegmentIterator(ring1, true);
        it2 = new SegmentIterator(ring2, false);
        fix(it1, it2);
        fix(it2, it1);    
        if (it1.isModified()) {
            ring1 = it1.getResult();
        }
        if (it2.isModified()) {
            ring2 = it2.getResult();
        }
    }
    
    private void fix(SegmentIterator it1, SegmentIterator it2) {
        it1.reset(0);
        while (it1.hasNext()) {
            Coordinate c1 = it1.next().p0;
            it2.reset(0);
            while (it2.hasNext()) {
                LineSegment ls2 = it2.next();
                if (ls2.distance(c1) < tolerance) {
                    if (ls2.p0.distance(c1) < tolerance) {
                        it2.updateStartPoint(c1);
                    }
                    else if (ls2.p1.distance(c1) < tolerance) {
                        it2.updateEndPoint(c1);
                    }
                    else {
                        it2.snap(c1, tolerance);
                    }
                }
            }
        }
    }

    public boolean ring1Modified() {
        return it1.isModified();
    }
    
    public boolean ring2Modified() {
        return it2.isModified();
    }

    public LinearRing getRing1() {
        return ring1;
    }

    public LinearRing getRing2() {
        return ring2;
    }
}
