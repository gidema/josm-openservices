package org.openstreetmap.josm.plugins.ods.jts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.crs.UnclosedWayException;

public class Boundary {
    private final static GeoUtil geoUtil = GeoUtil.getInstance();
    
    private MultiPolygon multiPolygon;
    private Collection<Bounds> bounds;
    private boolean rectangular;

    public Boundary(Way way) {
        super();
        if (!way.isClosed()) {
            throw new IllegalArgumentException();
        }
        try {
            LinearRing ring = geoUtil.toLinearRing(way);
            rectangular = isRectangular(ring);
            Polygon polygon = geoUtil.createPolygon(ring, null);
            this.multiPolygon = geoUtil.toMultiPolygon(polygon);
        } catch (UnclosedWayException e) {
            // Won't happen because we know the way is closed
        }
    }
    
    public Boundary(Bounds bounds) {
        this.bounds = Collections.singletonList(bounds);
        rectangular = true;
        List<Coordinate> coords = new ArrayList<>(5);
        coords.add(new Coordinate(bounds.getMinLon(), bounds.getMinLat()));
        coords.add(new Coordinate(bounds.getMinLon(), bounds.getMaxLat()));
        coords.add(new Coordinate(bounds.getMaxLon(), bounds.getMaxLat()));
        coords.add(new Coordinate(bounds.getMaxLon(), bounds.getMinLat()));
        coords.add(new Coordinate(bounds.getMinLon(), bounds.getMinLat()));
        LinearRing ring = geoUtil.toLinearRing(coords);
        Polygon polygon = geoUtil.createPolygon(ring, null);
        this.multiPolygon = geoUtil.toMultiPolygon(polygon);
    }

    public Boundary(MultiPolygon multiPolygon) {
        this.multiPolygon = multiPolygon;
        this.bounds = new ArrayList<>(multiPolygon.getNumGeometries());
        for (int i=0; i<multiPolygon.getNumGeometries(); i++) {
            Envelope envelope = multiPolygon.getGeometryN(i).getEnvelopeInternal();
            bounds.add(toBounds(envelope));
        }
        rectangular = isRectangular(multiPolygon);
    }

    public boolean isRectangular() {
        return rectangular;
    }
    
    public LinearRing getRing() {
        if (rectangular) {
            return ((Polygon)multiPolygon.getGeometryN(0)).getExteriorRing();
        }
        throw new UnsupportedOperationException("The boundary is not a rectangle");
    }
    
    public MultiPolygon getMultiPolygon() {
        return multiPolygon;
    }
    
    public Envelope getEnvelope() {
        return multiPolygon.getEnvelopeInternal();
    }
    
    public Collection<Bounds> getBounds() {
        return bounds;
    }

    private static Bounds toBounds(Envelope e) {
        return new Bounds(e.getMinY(), e.getMinX(), e.getMaxY(), e.getMaxX());
    }
    
    private static boolean isRectangular(MultiPolygon mp) {
        if (mp.getNumGeometries() != 1) return false;
        return isRectangular((Polygon) mp.getGeometryN(0));
    }
    
    private static boolean isRectangular(Polygon polygon) {
        if (polygon.getNumInteriorRing() != 0) return false;
        return isRectangular(polygon.getExteriorRing());
    }
    
    private static boolean isRectangular(LinearRing ring) {
        Polygon envelope = (Polygon)ring.getEnvelope();
        return envelope.equalsTopo(ring);
    }
    /**
     * Transform the boundary to the Coordinate reference system specified by the srid
     *      * 
     * @param crsUtil The CRSUtil instance to use for the transformation
     * @param srid The (EPSG) srid
     * @return
     */
    public Boundary transform(CRSUtil crsUtil, Long srid) {
        MultiPolygon mpg = (MultiPolygon) crsUtil.transform(multiPolygon, srid);
        return new Boundary(mpg);
    }
}
