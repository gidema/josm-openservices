package org.openstreetmap.josm.plugins.ods.wfs;

import java.io.IOException;

import org.openstreetmap.josm.plugins.ods.wfs.query.WfsRequest;

/**
 * Read a single page (batch) of features from a WFS datasource.
 * 
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface WfsPageReader {
    /**
     * Read the features and return them in a WfsFeatureCollection
     * 
     * @param query The query for the next page
     * @param progressListener May be null
     * @return
     *     The WfsFeatureCollection containing the downloaded features.
     *     The collection may be empty.
     * @throws IOException
     */
    WfsFeatureCollection read(WfsRequest request) throws IOException;
}
