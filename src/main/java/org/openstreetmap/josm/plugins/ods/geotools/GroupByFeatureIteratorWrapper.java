package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.List;
import java.util.NoSuchElementException;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

public class GroupByFeatureIteratorWrapper implements SimpleFeatureIterator {
    private final SimpleFeatureIterator wrapped;
    private int keyIndex;
    private SimpleFeature cachedFeature = null;
    private boolean hasNext = false;

    public GroupByFeatureIteratorWrapper(SimpleFeatureIterator wrapped, List<String> groupBy) {
        super();
        this.wrapped = wrapped;
        this.hasNext = wrapped.hasNext();
        if (hasNext) {
            cachedFeature = wrapped.next();
            keyIndex = cachedFeature.getFeatureType().indexOf(groupBy.get(0));
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
        if (wrapped.hasNext()) {
            SimpleFeature nextFeature = wrapped.next();
            if (!nextFeature.getAttribute(keyIndex)
                .equals(cachedFeature.getAttribute(keyIndex))) {
                SimpleFeature result = cachedFeature;
                cachedFeature = nextFeature;
                return result;
            }
            else {
                while (wrapped.hasNext() && 
                    (!nextFeature.getAttribute(keyIndex)
                .equals(cachedFeature.getAttribute(keyIndex)))) {
                    cachedFeature = nextFeature;
                    nextFeature = wrapped.next();
                }
            }
        }
        hasNext = wrapped.hasNext();
        return cachedFeature;
    }

    @Override
    public void close() {
        wrapped.close();
    }
}
