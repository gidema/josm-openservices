package org.openstreetmap.josm.plugins.ods.wfs;

import java.io.IOException;

import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.io.DataCutOffException;
import org.openstreetmap.josm.plugins.ods.wfs.query.WfsRequest;

public class SimpleWfsFeatureReader implements WfsFeatureReader {
    private final OdsContext context;
    private final WfsFeatureSource featureSource;
    private final WfsRequest baseRequest;
    private final long pageSize;

    public SimpleWfsFeatureReader(WfsFeatureSource featureSource, WfsRequest baseRequest, OdsContext context) {
        super();
        this.context = context;
        this.featureSource = featureSource;
        this.baseRequest = baseRequest;
        this.pageSize = baseRequest.getPageSize();
    }

    @Override
    public WfsFeatureCollection read() throws IOException {
        WfsPageReader pageReader = new DefaultWFSPageReader(featureSource, context);
        // TODO run this in a separate thread
        WfsFeatureCollection featureCollections = pageReader.read(baseRequest);
        if (featureCollections.getFeatureCount() != featureCollections.getNumberReturned()) {
            throw new IOException(String.format("The actual number of WFS features (%d) doesn't match the reported number of features (%d).",
                    featureCollections.getFeatureCount(), featureCollections.getNumberReturned()));
        }
        if (featureCollections.getFeatureCount() < pageSize) return featureCollections;
        throw new DataCutOffException();
    }
}
