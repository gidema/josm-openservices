package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class GroupByFeatureIterator extends OdsFeatureIterator {
    private int[] keyIndexes;
    private List<SimpleFeature> currentGroup;
    private boolean hasNext = false;

    public GroupByFeatureIterator(SimpleFeatureIterator wrapped, GroupByQuery query) {
        super(wrapped);
        List<String> groupAttributes = query.getGroupBy();
        keyIndexes = new int[groupAttributes.size()];
        this.hasNext = super.hasNext();
        if (hasNext) {
            currentGroup = new LinkedList<>();
            SimpleFeature firstFeature = super.next();
            currentGroup.add(firstFeature);
            SimpleFeatureType featureType = firstFeature.getFeatureType();
            for (int i = 0; i < groupAttributes.size(); i++) {
                int index = featureType.indexOf(groupAttributes.get(i));
                assert (index != -1 ); // This should have been checked 
                keyIndexes[i] = index;
            }
        }
    }
    
    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public SimpleFeature next() throws NoSuchElementException {
        if (!hasNext) {
            throw  new NoSuchElementException();
        }
        if (!super.hasNext()) {
            hasNext = false;
            return getAggregate(currentGroup);
        }
        SimpleFeature nextFeature = super.next();
        while (hasSameKey(nextFeature) && super.hasNext()) {
            currentGroup.add(nextFeature);
            nextFeature = super.next();
        }
        SimpleFeature result = getAggregate(currentGroup);
        currentGroup.clear();
        currentGroup.add(nextFeature);
        return result;
    }
    
    protected SimpleFeature getAggregate(List<SimpleFeature> featureGroup) {
        // Default behaviour: return the first row
        return featureGroup.get(0);
    }

    private boolean hasSameKey(SimpleFeature f) {
        SimpleFeature current = currentGroup.get(0);
        for (int i = 0; i < keyIndexes.length; i++) {
            if (!Objects.equals(f.getAttribute(i), current.getAttribute(i))) {
                return false;
            }
        }
        return true;
    }
}
