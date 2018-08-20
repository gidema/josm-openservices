package org.openstreetmap.josm.plugins.ods.geotools;

import org.opengis.filter.Filter;

public interface GtQuery {
    public String[] getProperties();
    public Filter getFilter();
    public int getPageSize();
}
