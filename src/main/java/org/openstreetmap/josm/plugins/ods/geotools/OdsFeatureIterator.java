package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.NoSuchElementException;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Basic wrapper around a SimpleFeatureIterator.
 * 
 * Add the ability to detect is if the data source has limited the number
 * of retrieved feature to a certain number;
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdsFeatureIterator implements SimpleFeatureIterator {
    private final SimpleFeatureIterator wrapped;
    private long limit = -1;
    private long featureCount;
    private boolean limited = false;
    
    public OdsFeatureIterator(SimpleFeatureIterator wrapped) {
        super();
        this.wrapped = wrapped;
        featureCount = 0;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    @Override
    public boolean hasNext() {
        return wrapped.hasNext();
    }

    @Override
    public SimpleFeature next() throws NoSuchElementException {
        SimpleFeature next = wrapped.next();
        featureCount++;
        if (limit != -1 && featureCount >= limit) {
            limited = true;
        }
        return next;
    }

    public boolean isLimited() {
        return limited;
    }

    @Override
    public void close() {
        wrapped.close();
    }
}
