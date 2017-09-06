package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.jts.LinearRingAligner;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class CrossingBuildingFixer {
    private final GeoUtil geoUtil;
    private final Double tolerance;

    private Building building1;
    private Building building2;
    
    public CrossingBuildingFixer(GeoUtil geoUtil, Double tolerance) {
        super();
        this.geoUtil = geoUtil;
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
        LinearRing ring1 = (LinearRing)polygon1.getExteriorRing();
        LinearRing ring2 = (LinearRing)polygon2.getExteriorRing();
        LinearRingAligner aligner = new LinearRingAligner(geoUtil, ring1, ring2, tolerance);
        aligner.run();
//        GeoUtil geoUtil = GeoUtil.getInstance();
        if (aligner.ring1Modified()) {
            building1.setGeometry(geoUtil.toPolygon(aligner.getRing1(), getInteriorRings(polygon1)));            
        }
        if (aligner.ring2Modified()) {
            building2.setGeometry(geoUtil.toPolygon(aligner.getRing2(), getInteriorRings(polygon2)));            
        }
    }
    
    @SuppressWarnings("static-method")
    private LinearRing[] getInteriorRings(Polygon polygon) {
        LinearRing[] rings = new LinearRing[polygon.getNumInteriorRing()];
        for (int i=0; i< polygon.getNumInteriorRing(); i++) {
            rings[i] = (LinearRing) polygon.getInteriorRingN(i);
        }
        return rings;
    }
}
