package org.openstreetmap.josm.plugins.ods.setup;

import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerDownloader;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.io.MainDownloader;
import org.openstreetmap.josm.plugins.ods.io.OsmLayerDownloader;

public interface ModuleSetup {

    OdLayerManager getOdLayerManager();
    OsmLayerManager getOsmLayerManager();
    OpenDataLayerDownloader getOdLayerDownloader();
    OsmLayerDownloader getOsmLayerDownloader();
    MainDownloader getMainDownloader();
}
