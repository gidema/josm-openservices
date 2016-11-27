package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geotools.data.Query;
import org.opengis.feature.FeatureVisitor;
import org.openstreetmap.josm.plugins.ods.DefaultOdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;

public class GtDataSource extends DefaultOdsDataSource {
    private List<FilterFactory> filters;

    public GtDataSource(OdsFeatureSource odsFeatureSource, Query query) {
        this(odsFeatureSource, query, new ArrayList<>());
    }

    public GtDataSource(OdsFeatureSource odsFeatureSource, Query query, List<FilterFactory> filters) {
        super(odsFeatureSource, query);
        this.filters = filters;
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
