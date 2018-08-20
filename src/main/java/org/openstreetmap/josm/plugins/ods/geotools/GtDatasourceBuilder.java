package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.Arrays;
import java.util.List;

import org.opengis.filter.Filter;
import org.openstreetmap.josm.plugins.ods.geotools.impl.SimpleQuery;

public class GtDatasourceBuilder {
    private GtFeatureSource featureSource;
    private List<String> properties;
    //    private final List<FilterFactory> filters = new LinkedList<>();
    private Filter filter;
    private int pageSize = 0;

    public void setFeatureSource(GtFeatureSource featureSource) {
        this.featureSource = featureSource;
    }

    public void setProperties(String ... properties) {
        this.properties = Arrays.asList(properties);
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public GtDataSource build() {
        SimpleQuery query = new SimpleQuery(filter, properties, pageSize);
        return new GtDataSource(featureSource, query);
    }
}
