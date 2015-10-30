package org.openstreetmap.josm.plugins.ods.jts;

import java.util.ArrayList;
import java.util.Collections;

import org.openstreetmap.josm.Main;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LinearRing;

public class SegmentIterator {
    private GeoUtil geoUtil;
    private LinearRing ring;
    // Use an arrayList because we use the get(i) method a lot.
    private ArrayList<Coordinate> coords;
    private boolean roundTrip = true;
    // Set to true if the order of the coordinates in the coords array
    // is reversed with respect to the order in the supplied linear ring
    private boolean reversed = false;
    private boolean modified = false;
    private LineSegment previousLs = null;
    private LineSegment currentLs = null;
    private LineSegment nextLs = null;
    private int index;
    private int startIndex; //

    public SegmentIterator(GeoUtil geoUtil, LinearRing ring, boolean ccw) {
        this.geoUtil = geoUtil;
        this.ring = ring;
        int size = ring.getNumPoints();
        coords = new ArrayList<Coordinate>(size + 5);
        if (CGAlgorithms.isCCW(ring.getCoordinates()) ^ ccw) {
            for (int i = size - 1; i >= 0; i--) {
                coords.add(ring.getCoordinateN(i));
            }
            reversed = true;
        } else {
            for (int i = 0; i < size; i++) {
                coords.add(ring.getCoordinateN(i));
            }
            reversed = false;
        }
        reset(0);
    }

    public boolean isModified() {
        return modified;
    }

    public boolean hasNext() {
        return roundTrip;
    }

    public LineSegment next() {
        if (currentLs == null) {
            previousLs = new LineSegment(coords.get(previousIndex()), coords.get(index));
            currentLs = new LineSegment(coords.get(index), coords.get(nextIndex()));
        }
        else {
            previousLs = currentLs;
            currentLs = nextLs;
        }
        index = nextIndex();
        nextLs = new LineSegment(coords.get(index), coords.get(nextIndex()));
        if (index == startIndex) {
            roundTrip = false;
        }
        return currentLs;
    }

    public LineSegment snap(Coordinate c, Double tolerance) {
        Double distance = currentLs.distance(c);
        if (distance > previousLs.distance(c) || distance > nextLs.distance(c)) {
            return currentLs;
        }
        if (c.distance(currentLs.p0) < tolerance) {
            updateStartPoint(c);
        }
        else if (c.distance(currentLs.p1) < tolerance) {
            updateEndPoint(c);
        }
        else {
            previousLs.p0 = currentLs.p0;
            previousLs.p1 = c;
            coords.add(index, c);
            if (index == 0) {
                // Make sure we keep the ring closed
                coords.set(coords.size() - 1, c);
            }
            if (startIndex > index) {
                startIndex++;
            }
            currentLs = new LineSegment(coords.get(index),
                    coords.get(nextIndex()));
            index = nextIndex();
            modified = true;
        }
        return currentLs;
    }

    /**
     * Change the start point of the current segment. If the new coordinate is
     * the same, nothing happens.
     * 
     * @param coord
     */
    public void updateStartPoint(Coordinate coord) {
        if (!currentLs.p0.equals(coord)) {
            previousLs.p1 = coord;
            currentLs = new LineSegment(coord, currentLs.p1);
            updatePoint(previousIndex(), coord);
        }
    }

    /**
     * Change the end point of the current segment. If the new coordinate is the
     * same, nothing happens.
     * 
     * @param coord
     */
    public void updateEndPoint(Coordinate coord) {
        if (!currentLs.p1.equals(coord)) {
            currentLs = new LineSegment(currentLs.p0, coord);
            nextLs.p0 = coord;
            updatePoint(index, coord);
        }
    }

    private void updatePoint(int index, Coordinate coord) {
        coords.set(index, coord);
        if (index == 0) {
            coords.set(coords.size() - 1, coord);
        }
        modified = true;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the previous index with respect to the currentLs index
     * 
     * @return
     */
    private int previousIndex() {
        return previousIndex(index);
    }

    /**
     * Get the previous index with respect to the supplied index
     * 
     * @return
     */
    private int previousIndex(int i) {
        int previousIndex = i - 1;
        if (previousIndex == -1) {
            previousIndex = coords.size() - 1;
        }
        return previousIndex;
    }

    /**
     * Get the next index with respect to the currentLs index
     * 
     * @return
     */
    private int nextIndex() {
        return nextIndex(index);
    }

    /**
     * Get the next index with respect to the supplied index
     * 
     * @return
     */
    private int nextIndex(int i) {
        int nextIndex = i + 1;
        if (nextIndex == coords.size() - 1) {
            nextIndex = 0;
        }
        return nextIndex;
    }

    public LinearRing getResult() {
        if (!modified) {
            return ring;
        }
        if (reversed) {
            Collections.reverse(coords);
        }
        try {
            return geoUtil.toLinearRing(coords);
        }
        catch (@SuppressWarnings("unused") IllegalArgumentException e) {
            Main.warn("Invalid ring. Not fixed.");
            return ring;
        }
    }

    public void reset(int index) {
        this.index = index;
        this.startIndex = index;
        this.roundTrip = true;
        currentLs = null;
    }

    public void reset() {
        startIndex = index;
    }

    public LineSegment getCurrent() {
        return currentLs;
    }

    public LineSegment getPrevious() {
        return previousLs;
    }

    public LineSegment getNext() {
        return nextLs;
    }
}
