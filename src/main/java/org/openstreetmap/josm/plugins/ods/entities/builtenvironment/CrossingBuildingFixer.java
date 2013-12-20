package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.crs.GeoUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class CrossingBuildingFixer {
    private Building building1;
    private Building building2;
    private final Double tolerance;
    
    public CrossingBuildingFixer(Double tolerance) {
        super();
        this.tolerance = tolerance;
    }

    public void setBuildings(Building building1, Building building2) {
        this.building1 = building1;
        this.building2 = building2;
    }
    
    public void fix() {
        Geometry geom1 = building1.getGeometry();
        Geometry geom2 = building2.getGeometry();
        if (!"Polygon".equals(geom1.getGeometryType()) ||
                !"Polygon".equals(geom2.getGeometryType())) {
            System.out.println("The crossing buildings fix only works for simple polygons");
            return;
        }
        Polygon polygon1 = (Polygon) geom1;
        Polygon polygon2 = (Polygon) geom2;
        List<Coordinate> coords1 = toList(polygon1.getExteriorRing().getCoordinates());
        List<Coordinate> coords2 = toList(polygon2.getExteriorRing().getCoordinates());
        fix(coords1, coords2);
        fix(coords2, coords1);
        GeoUtil geoUtil = GeoUtil.getInstance();
        building1.setGeometry(geoUtil.toPolygon(coords1, getInteriorRings(polygon1)));
        building2.setGeometry(geoUtil.toPolygon(coords2, getInteriorRings(polygon2)));
    }
    
    private void fix(List<Coordinate> coords1, List<Coordinate> coords2) {
        for (Coordinate coord1 : coords1) {
            LineSegment segment = new LineSegment(coords2.get(0), coords2.get(0));
            int i = 1;
            while (i<coords2.size()) {
                segment.p0 = segment.p1;
                segment.p1 = coords2.get(i);
                Double dist = segment.distance(coord1);
                if (dist != 0 && dist < tolerance) {
                    if (segment.p1.distance(coord1) < tolerance) {
                        segment.p1 = coord1;
                        coords2.set(i, coord1);
                    }
                    else {
                        coords2.add(i, coord1);
                        segment.p1 = coord1;
                    }
                }
                i++;
            }
        }
    }
    
    private LinkedList<Coordinate> toList(Coordinate[] coords) {
        LinkedList<Coordinate> list = new LinkedList<>();
        for (Coordinate coord : coords) {
            list.add(coord);
        }
        return list;
    }
    
    private LinearRing[] getInteriorRings(Polygon polygon) {
        LinearRing[] rings = new LinearRing[polygon.getNumInteriorRing()];
        for (int i=0; i< polygon.getNumInteriorRing(); i++) {
            rings[i] = (LinearRing) polygon.getInteriorRingN(i);
        }
        return rings;
    }
}
