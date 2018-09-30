package org.openstreetmap.josm.plugins.ods.setup;

import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.io.OsmLayerDownloader;

public class SimpleOsmSetup implements OsmSetup {
    private final OsmLayerManager osmLayerManager;
    private OsmLayerDownloader osmLayerDownloader;

    /**
     * Basic implementation of OsmSetup for ODS modules that merely download
     * data from the Open data sources without comparison to, or updating of the
     * OSM layer.

     * @param osmLayerManager
     */
    public SimpleOsmSetup(OsmLayerManager osmLayerManager) {
        super();
        this.osmLayerManager = osmLayerManager;
        setup();
    }

    protected void setup() {
        this.osmLayerDownloader = new OsmLayerDownloader(osmLayerManager, null, null);
    }

    @Override
    public OsmLayerDownloader getOsmLayerDownloader() {
        return osmLayerDownloader;
    }
}
