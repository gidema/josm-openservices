package org.openstreetmap.josm.plugins.ods.crs.test;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

public class CoordinateTransformTest {

    public CoordinateTransformTest() {
        // TODO Auto-generated constructor stub
    }

    @SuppressWarnings("static-method")
    @Test
    public void testTransform() throws Exception {
        CoordinateReferenceSystem sourceCRS;
        CoordinateReferenceSystem targetCRS;

        sourceCRS = CRS.decode("EPSG:28992");
        targetCRS = CRS.decode("EPSG:4326");

        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
        Coordinate source = new Coordinate(155000, 460000);
        Coordinate destination = new Coordinate(0, 0);
        JTS.transform(source, destination, transform);
        System.out.println(destination.toString());
    }
}
