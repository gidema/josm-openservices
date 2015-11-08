package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.List;
import java.util.NoSuchElementException;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

public class GroupByFeatureIteratorWrapper implements SimpleFeatureIterator {
    private final SimpleFeatureIterator wrapped;
    private final List<String> groupBy;
    private int keyIndex;
    private SimpleFeature currentFeature = null;
    private boolean done = false;

    public GroupByFeatureIteratorWrapper(SimpleFeatureIterator wrapped, List<String> groupBy) {
        super();
        this.wrapped = wrapped;
        this.groupBy = groupBy;
    }

    @Override
    public boolean hasNext() {
        if (currentFeature == null) {
            if (!wrapped.hasNext()) {
                return false;
            }
            currentFeature = wrapped.next();
            keyIndex = currentFeature.getFeatureType().indexOf(groupBy.get(0));
        }
        return !done;
    }

    @Override
    public SimpleFeature next() throws NoSuchElementException {
        if (!wrapped.hasNext()) {
            done = true;
            return currentFeature;
        }
        SimpleFeature nextFeature;
        do {
            nextFeature = wrapped.next();
        } while (wrapped.hasNext() && nextFeature.getAttribute(keyIndex)
                .equals(currentFeature.getAttribute(keyIndex)));
        if (!wrapped.hasNext()) {
            done = true;
            return currentFeature;
        }
        SimpleFeature result = currentFeature;
        currentFeature = nextFeature;
        return result;
    }

    @Override
    public void close() {
        wrapped.close();
    }
}
