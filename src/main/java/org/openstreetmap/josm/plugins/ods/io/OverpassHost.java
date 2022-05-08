package org.openstreetmap.josm.plugins.ods.io;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.Preferences;
import org.openstreetmap.josm.io.BoundingBoxDownloader;
import org.openstreetmap.josm.io.OsmServerReader;
import org.openstreetmap.josm.io.OverpassDownloadReader;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LinearRing;

public class OverpassHost implements OsmHost {
    private static final String OVERPASS_QUERY =
            "(node({{bbox}});rel(bn)->.x;way({{bbox}});" +
                    "node(w)->.x;rel(bw););out meta;";

    @Override
    public String getHostString() {
        String host = Preferences.main().get("download.overpass.server");
        if (host == null || host.isEmpty()) {
            host = "https://overpass-api.de/api/";
        }
        // Make sure the host string ends with a forward slash.
        if (!host.endsWith("/")) {
            host = host + "/";
        }
        return host;
    }

    // TODO Currently, we create an extra server reader for each boundary.
    // Check if we can replace this with a single multipolygon request.
    @Override
    public Collection<OsmServerReader> getServerReaders(DownloadRequest request) {
        List<OsmServerReader> serverReaders = new ArrayList<>(request.getBoundary().getBounds().size());
        request.getBoundary().getBounds().forEach(bounds -> {
            serverReaders.add(new OverpassDownloadReader(bounds, getHostString(), OVERPASS_QUERY));
        });
        return serverReaders;
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
            return getBBox(boundary.getBounds().iterator().next());
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
