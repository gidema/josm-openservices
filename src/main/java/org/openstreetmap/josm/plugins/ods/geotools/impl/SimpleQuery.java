package org.openstreetmap.josm.plugins.ods.geotools.impl;

import java.util.List;

import org.opengis.filter.Filter;
import org.openstreetmap.josm.plugins.ods.geotools.GtQuery;

public class SimpleQuery implements GtQuery {
    private final String[] properties;
    private final Filter filter;
    private final int pageSize;

    public SimpleQuery(Filter filter, List<String> properties, int pageSize) {
        super();
        this.properties = properties.toArray(new String[0]);
        this.filter = filter;
        this.pageSize = pageSize;
    }

    @Override
    public String[] getProperties() {
        return properties;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }
}
