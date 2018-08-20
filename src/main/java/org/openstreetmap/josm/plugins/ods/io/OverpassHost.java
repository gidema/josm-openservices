package org.openstreetmap.josm.plugins.ods.io;

import java.net.MalformedURLException;
import java.util.Locale;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.Preferences;
import org.openstreetmap.josm.io.OsmServerReader;
import org.openstreetmap.josm.io.OverpassDownloadReader;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LinearRing;

public class OverpassHost implements OsmHost {
    private static final String OVERPASS_QUERY =
            "(node({{bbox}});rel(bn)->.x;way({{bbox}});" +
                    "node(w)->.x;rel(bw););out meta;";

    @Override
    public String getHostString() {
        String host = Preferences.main().get("download.overpass.server");
        if (host == null || host.isEmpty()) {
            host = "https://overpass-api.de/api";
        }
        // Make sure the host string ends with a forward slash.
        if (!host.endsWith("/")) {
            host = host + "/";
        }
        return host;
    }

    @Override
    public OsmServerReader getServerReader(DownloadRequest request) throws MalformedURLException {
        return new OverpassDownloadReader(request.getBoundary().getBounds(),
                getHostString(), OVERPASS_QUERY);
    }

    /**
     * The current implementation is based on the Josm built-in OverpassDownloadReader
     * class. This class doesn't support polygon-based downloading.
     *
     * @see org.openstreetmap.josm.plugins.ods.io.OsmHost#supportsPolygon()
     */
    @Override
    public boolean supportsPolygon() {
        return false;
    }

    /**
     * Create an overpass bounding box string from a JTS boundary object
     * @param boundary
     * @return
     */
    public static String getBoundary(Boundary boundary) {
        if (boundary.isRectangular()) {
            return getBBox(boundary.getBounds());
        }
        return getBBox(boundary.getRing());
    }

    /**
     * Create an overpass bounding box String from a JTS Bounds object.
     *
     * @param bounds
     * @return
     */
    private static String getBBox(Bounds bounds) {
        return String.format(Locale.ENGLISH, "%f,%f,%f,%f", bounds
                .getMin().getY(), bounds.getMin().getX(), bounds.getMax()
                .getY(), bounds.getMax().getX());
    }

    /**
     * Create an overpass bounding box String from a JTS LinearRing object
     * @param ring
     * @return
     */
    private static String getBBox(LinearRing ring) {
        StringBuilder sb = new StringBuilder(1000);
        sb.append("poly:\"");
        Coordinate[] coords = ring.getCoordinates();
        for (Coordinate coord : coords) {
            sb.append(formatCoordinate(coord.y, coord.x));
        }
        // Remove last space to fix issue #57
        sb.setLength(sb.length() - 1);
        sb.append("\"");
        return sb.toString();
    }

    private static String formatCoordinate(Double lat, Double lon) {
        return String.format(Locale.ENGLISH, "%f %f ", lat, lon);
    }
}
