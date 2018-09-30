package org.openstreetmap.josm.plugins.ods.geotools;

import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class GtDataSource implements OdsDataSource {
    private final GtFeatureSource gtFeatureSource;
    private final GtQuery query;
    private boolean initialized;
    private boolean required;

    public GtDataSource(GtFeatureSource gtFeatureSource, GtQuery query) {
        super();
        this.gtFeatureSource = gtFeatureSource;
        this.query = query;
    }

    @Override
    public final GtFeatureSource getFeatureSource() {
        return gtFeatureSource;
    }

    public void initialize() throws InitializationException {
        if (!initialized) {
            gtFeatureSource.initialize();
            initialized = true;
        }
    }

    @Override
    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    public GtQuery getQuery() {
        return query;
    }

    @Override
    public String getFeatureType() {
        return gtFeatureSource.getFeatureName();
    }

    @Override
    public MetaData getMetaData() {
        return getFeatureSource().getMetaData();
    }
}
