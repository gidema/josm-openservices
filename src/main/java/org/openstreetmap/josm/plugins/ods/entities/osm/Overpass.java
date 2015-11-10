package org.openstreetmap.josm.plugins.ods.entities.osm;

import java.util.Locale;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LinearRing;

public class Overpass {
    public static String getURL(String query, Boundary boundary) {
        String host = "http://overpass-api.de/api";
        String bbox = getBoundary(boundary);
        String q = query.replaceAll("\\$bbox", bbox);
        q = q.replaceAll("\\{\\{bbox\\}\\}", bbox);
        q = q.replace(";$", "");
        return String.format("%s/interpreter?data=%s;out meta;", host, q);
    }

    public static String getBoundary(Boundary boundary) {
        if (boundary.isRectangular()) {
            return getBoundary(boundary.getBounds());
        }
        return getBoundary(boundary.getRing());
    }
    
    private static String getBoundary(Bounds bounds) {
        return String.format(Locale.ENGLISH, "%f,%f,%f,%f", bounds
             .getMin().getY(), bounds.getMin().getX(), bounds.getMax()
             .getY(), bounds.getMax().getX());
    }

    public static String getBoundary(LinearRing ring) {
        StringBuilder sb = new StringBuilder(1000);
        sb.append("poly:\"");
        Coordinate[] coords = ring.getCoordinates();
        for (Coordinate coord : coords) {
            sb.append(formatCoordinate(coord.y, coord.x));
        }
        sb.append("\"");
        return sb.toString();
    }

    
    public static String formatCoordinate(Double lat, Double lon) {
        return String.format(Locale.ENGLISH, "%f %f ", lat, lon);
    }
    
    public static String formatDouble(Double d) {
        return String.format(Locale.ENGLISH, "%f ", d);
    }

}
