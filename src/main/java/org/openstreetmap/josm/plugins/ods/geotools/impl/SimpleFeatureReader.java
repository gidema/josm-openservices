package org.openstreetmap.josm.plugins.ods.geotools.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Consumer;

import org.geotools.data.Query;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.util.ProgressListener;
import org.openstreetmap.josm.plugins.ods.geotools.DataCutOffException;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureReader;
import org.openstreetmap.josm.plugins.ods.geotools.GtPageReader;

public class SimpleFeatureReader implements GtFeatureReader {
    private final GtDataSource dataSource;
    private final Query baseQuery;
    private final long maxFeatures;

    public SimpleFeatureReader(GtDataSource dataSource, Query query) {
        super();
        this.dataSource = dataSource;
        this.baseQuery = query;
        this.maxFeatures = dataSource.getOdsFeatureSource().getMaxFeatureCount();
    }

    @Override
    public void read(Consumer<SimpleFeature> consumer, ProgressListener progressListener) throws IOException {
        GtPageReader pageReader = new DefaultGtPageReader(dataSource.getOdsFeatureSource().getFeatureSource());
        // TODO run this in a separate thread
        Collection<SimpleFeature> features = pageReader.read(baseQuery, progressListener);
        if (features.size() < maxFeatures || maxFeatures == 0) {
            features.forEach(consumer);
        }
        else {
            throw new DataCutOffException();
        }
    }
}
