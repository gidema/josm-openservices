package org.openstreetmap.josm.plugins.ods.geotools.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.util.ProgressListener;
import org.openstreetmap.josm.plugins.ods.geotools.GtPageReader;

/**
 * Default implementation of the GtPageReader class.
 *
 * @See GtPageReader
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class DefaultGtPageReader implements GtPageReader {
    private final SimpleFeatureSource featureSource;

    public DefaultGtPageReader(SimpleFeatureSource featureSource) {
        super();
        this.featureSource = featureSource;
    }

    @Override
    public Collection<SimpleFeature> read(Query query, ProgressListener progressListener) throws IOException {
        SimpleFeatureCollection featureCollection = featureSource.getFeatures(query);
        List<SimpleFeature> features = new LinkedList<>();
        try (
                SimpleFeatureIterator it = featureCollection.features();
                ) {
            while (it.hasNext()) {
                features.add(it.next());
            }
        }
        return features;
    }
}
