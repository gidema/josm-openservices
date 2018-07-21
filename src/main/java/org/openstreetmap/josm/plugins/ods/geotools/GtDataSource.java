package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geotools.data.Query;
import org.opengis.feature.FeatureVisitor;
import org.openstreetmap.josm.plugins.ods.DefaultIdFactory;
import org.openstreetmap.josm.plugins.ods.IdFactory;
import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class GtDataSource implements OdsDataSource {
    private final GtFeatureSource gtFeatureSource;
    private final Query query;
    private IdFactory idFactory;
    private boolean initialized;
    private boolean required;
    private final int pageSize;
    private final List<FilterFactory> filters;

    public GtDataSource(GtFeatureSource gtFeatureSource, int pageSize, Query query) {
        this(gtFeatureSource, pageSize, query, new ArrayList<>());
    }

    public GtDataSource(GtFeatureSource gtFeatureSource, int pageSize, Query query, List<FilterFactory> filters) {
        super();
        this.gtFeatureSource = gtFeatureSource;
        this.query = query;
        this.pageSize = pageSize;
        this.filters = filters;
    }

    @Override
    public final GtFeatureSource getOdsFeatureSource() {
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

    @Override
    public Query getQuery() {
        return query;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public void setIdFactory(DefaultIdFactory idFactory) {
        this.idFactory = idFactory;
    }

    /**
     * @see org.openstreetmap.josm.plugins.ods.OdsDataSource#getIdFactory()
     */
    @Deprecated @Override
    public IdFactory getIdFactory() {
        if (idFactory == null) {
            idFactory = new DefaultIdFactory(
                    getOdsFeatureSource().getIdAttribute());
        }
        return idFactory;
    }

    @Override
    public String getFeatureType() {
        return gtFeatureSource.getFeatureName();
    }

    @Override
    public MetaData getMetaData() {
        return getOdsFeatureSource().getMetaData();
    }

    /**
     * Build a FeatureVisitor that chains any filters in the right order.
     *
     * @param consumer
     * @return
     */
    public FeatureVisitor createVisitor(FeatureVisitor consumer) {
        if (filters.isEmpty()) {
            return consumer;
        }
        ArrayList<FilterFactory> filterFactories = new ArrayList<>(filters);
        Collections.reverse(filterFactories);
        FeatureVisitor result = consumer;
        for (FilterFactory factory : filterFactories) {
            FilteringFeatureVisitor visitor = factory.instance();
            visitor.setConsumer(result);
            result = visitor;
        }
        return result;
    }
}
