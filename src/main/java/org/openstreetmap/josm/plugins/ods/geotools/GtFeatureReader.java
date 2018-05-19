package org.openstreetmap.josm.plugins.ods.geotools;

import java.io.IOException;
import java.util.function.Consumer;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.util.ProgressListener;

/**
 * Geotools feature reader that supports paging.
 * Paging means that the complete results may be retrieved in several smaller
 * batches (pages), to improve performance.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface GtFeatureReader {
    void read(Consumer<SimpleFeature> consumer, ProgressListener progressListener)
            throws IOException;
}
