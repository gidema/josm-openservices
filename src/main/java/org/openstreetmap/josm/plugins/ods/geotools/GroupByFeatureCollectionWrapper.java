package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.Iterator;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.collection.AbstractFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;

public class GroupByFeatureCollectionWrapper extends AbstractFeatureCollection {
    private final SimpleFeatureCollection wrapped;
    private final GroupByQuery query;

    public GroupByFeatureCollectionWrapper(SimpleFeatureCollection wrapped, GroupByQuery query) {
        super(wrapped.getSchema());
        this.wrapped = wrapped;
        this.query = query;
    }

    @Override
    protected Iterator<SimpleFeature> openIterator() {
        return new SimpleFeatureIteratorIterator(
            new GroupByFeatureIteratorWrapper(wrapped.features(), query.getGroupBy()));
    }
    
    @Override
    public int size() {
        // TODO This result is not accurate, because the duplicate features are counted;
        return wrapped.size();
    }

    @Override
    public ReferencedEnvelope getBounds() {
        return wrapped.getBounds();
    }

    class SimpleFeatureIteratorIterator implements Iterator<SimpleFeature>, SimpleFeatureIterator {
        
        SimpleFeatureIterator fi;
        
        public SimpleFeatureIteratorIterator(SimpleFeatureIterator fi) {
            super();
            this.fi = fi;
        }

        @Override
        public boolean hasNext() {
            return fi.hasNext();
        }

        @Override
        public SimpleFeature next() {
            return fi.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void close() {
            fi.close();
        }
    }
}
