package org.openstreetmap.josm.plugins.ods.parsing;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public interface FeatureParser {
    void parse(SimpleFeatureCollection downloadedFeatures, DownloadResponse response);
}
