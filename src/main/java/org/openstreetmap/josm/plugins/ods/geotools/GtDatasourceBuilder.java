package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.geotools.data.Query;
import org.opengis.filter.Filter;

public class GtDatasourceBuilder {
    private GtFeatureSource featureSource;
    private List<String> properties;
    private List<String> uniqueKey;
    private List<FilterFactory> filters = new LinkedList<>();
    private int pageSize = 0;
    
    public void setFeatureSource(GtFeatureSource featureSource) {
        this.featureSource = featureSource;
    }
    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    public void setUniqueKey(String property) {
        setUniqueKey(Arrays.asList(property));
    }

    public void setUniqueKey(List<String> uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    public GtDataSource build() {
        Query query = createQuery();
        if (uniqueKey != null) {
            filters.add(new UniqueKeyFilterFactory(uniqueKey));
        }
        return new GtDataSource(featureSource, pageSize, query, filters);
    }
    
    private Query createQuery() {
        return new Query(featureSource.getFeatureName(), Filter.INCLUDE, properties.toArray(new String[0]));
    }
}
