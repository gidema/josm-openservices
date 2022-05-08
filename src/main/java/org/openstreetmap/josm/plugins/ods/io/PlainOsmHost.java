package org.openstreetmap.josm.plugins.ods.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openstreetmap.josm.data.Preferences;
import org.openstreetmap.josm.io.BoundingBoxDownloader;
import org.openstreetmap.josm.io.OsmServerReader;

public class PlainOsmHost implements OsmHost {

    @Override
    public String getHostString() {
        String host = Preferences.main().get("osm-server.url");
        if (host == null) {
            host = "https://api.openstreetmap.org/api";
        }
        return host;
    }

    @Override
    public boolean supportsPolygon() {
        return false;
    }

    @Override
    public Collection<OsmServerReader> getServerReaders(DownloadRequest request) {
        List<OsmServerReader> serverReaders = new ArrayList<>(request.getBoundary().getBounds().size());
        request.getBoundary().getBounds().forEach(bounds -> {
            serverReaders.add(new BoundingBoxDownloader(bounds));
        });
        return serverReaders;
    }
}
