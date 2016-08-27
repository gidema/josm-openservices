package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.LinkedList;
import java.util.List;

import org.geotools.data.Query;
import org.geotools.filter.FilterFactoryImpl;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.openstreetmap.josm.tools.I18n;

public class GroupByQuery extends Query {
    // TODO Use Hints to discover the default filterFactory 
    private FilterFactory ff = new FilterFactoryImpl();
    private List<String> groupBy;
    private SortBy[] sortByArr = null;

    public GroupByQuery(GtFeatureSource featureSource, List<String> groupBy) throws InvalidQueryException {
        super(featureSource.getFeatureName(), Filter.INCLUDE);
        this.groupBy = groupBy;
        checkAttributes(featureSource);
    }

    public List<String> getGroupBy() {
        return groupBy;
    }

    @Override
    public SortBy[] getSortBy() {
        if (sortByArr == null) {
            List<SortBy> sortList = new LinkedList<>();
            for (String attName : groupBy) {
                SortBy sortBy = ff.sort(attName, SortOrder.ASCENDING);
                if (sortBy != null) {
                    sortList.add(sortBy);
                }
            }
            sortByArr = sortList.toArray(new SortBy[0]);
        }
        return sortByArr;
    }
    
    private void checkAttributes(GtFeatureSource featureSource) throws InvalidQueryException {
        FeatureType featureType = featureSource.getFeatureType();
        List<String> unknownAttributes = new LinkedList<>();
        for (String attribute : groupBy) {
            if (featureType.getDescriptor(attribute) == null) {
                unknownAttributes.add(attribute);
            }
        }
        if (unknownAttributes.size() > 0) {
            StringBuilder sb = new StringBuilder(I18n.tr(
                    "One or more query attributes are unkown for feature ''{0}'': ", featureType.getName()));
            sb.append(String.join(", ", unknownAttributes));
            throw new InvalidQueryException(sb.toString());
        }
    }


}
