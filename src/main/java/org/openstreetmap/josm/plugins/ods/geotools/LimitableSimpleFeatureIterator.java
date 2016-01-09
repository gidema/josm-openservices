package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.NoSuchElementException;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Implementation of a SimpleFeatureIterator that wrap around an other
 * SimpleFeatureIterator and throw a DataCutOff exception if the data
 * was limited to a maximum number of features.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class _LimitableSimpleFeatureIterator implements SimpleFeatureIterator {
    private SimpleFeatureIterator wrapped;
    private long maxFeatures = -1;
    private long retreivedFeatures = 0;
    
    public LimitableSimpleFeatureIterator(SimpleFeatureIterator wrappedIterator,
            long maxFeatures) {
        super();
        this.wrapped = wrappedIterator;
        this.maxFeatures = maxFeatures;
    }

    @Override
    public boolean hasNext() {
        return wrapped.hasNext();
    }

    @Override
    public SimpleFeature next() throws NoSuchElementException, DataCutOffException {
        SimpleFeature next = wrapped.next();
        retreivedFeatures++;
        if (retreivedFeatures == maxFeatures) {
             throw new DataCutOffException();
        }
        return next;
    }

    @Override
    public void close() {
        wrapped.close();
    }
}
