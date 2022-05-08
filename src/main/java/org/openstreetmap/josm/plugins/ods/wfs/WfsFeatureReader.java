package org.openstreetmap.josm.plugins.ods.wfs;

import java.io.IOException;

/**
 * Wfs feature reader that supports paging.
 * Paging means that the complete results may be retrieved in several smaller
 * batches (pages), to improve performance.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface WfsFeatureReader {
    public WfsFeatureCollection read() throws IOException;
}
