package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.LinkedList;
import java.util.List;

import org.geotools.data.Query;
import org.geotools.filter.FilterFactoryImpl;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;

public class GroupByQuery extends Query {
    // TODO Use Hints to discover the default filterFactory 
    private FilterFactory ff = new FilterFactoryImpl();
    private List<String> groupBy;
    private SortBy[] sortByArr = null;

    public GroupByQuery(String typeName, List<String> groupBy) {
        super(typeName, Filter.INCLUDE);
        this.groupBy = groupBy;
    }

    public List<String> getGroupBy() {
        return groupBy;
    }

    @Override
    public SortBy[] getSortBy() {
        List<SortBy> sortList = new LinkedList<>();
        if (sortByArr == null) {
            for (String attName : groupBy) {
                SortBy sortBy = ff.sort(attName, SortOrder.ASCENDING);
                if (sortBy != null) {
                    sortList.add(sortBy);
                }
            }
        }
        return sortList.toArray(new SortBy[0]);
    }
}
