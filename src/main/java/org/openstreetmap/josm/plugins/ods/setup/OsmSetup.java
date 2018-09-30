package org.openstreetmap.josm.plugins.ods.setup;

import org.openstreetmap.josm.plugins.ods.io.OsmLayerDownloader;

/**
 * Set up of the OSM layer side of the module.
 * In this class, stuff like the conversion of OSM primitives to ODS entities is
 * configured.
 * The SimpleOsmSetup implementation is provided for ODS modules that merely download
 * data from the Open data sources without comparison to, or updating of the
 * OSM layer.
 *
 * @author Gertjan Idema
 *
 */
public interface OsmSetup {

    public OsmLayerDownloader getOsmLayerDownloader();
}