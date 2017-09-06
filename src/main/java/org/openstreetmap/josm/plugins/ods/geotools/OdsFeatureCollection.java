package org.openstreetmap.josm.plugins.ods.geotools;

import java.io.IOException;
import java.util.Collection;

import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.opengis.util.ProgressListener;

public class OdsFeatureCollection implements SimpleFeatureCollection {
    private final SimpleFeatureCollection wrapped;
    private long featureCountLimit = -1;
    
    public OdsFeatureCollection(SimpleFeatureCollection wrapped) {
        super();
        this.wrapped = wrapped;
    }

    public void setFeatureCountLimit(long featureCountLimit) {
        this.featureCountLimit = featureCountLimit;
    }


    @Override
    public SimpleFeatureType getSchema() {
        return wrapped.getSchema();
    }

    @Override
    public String getID() {
        return wrapped.getID();
    }

    @Override
    public void accepts(FeatureVisitor visitor, ProgressListener progress)
            throws IOException {
        DataUtilities.visit(this, visitor, progress);
    }

    @Override
    public ReferencedEnvelope getBounds() {
        return wrapped.getBounds();
    }

    @Override
    public boolean contains(Object o) {
        return wrapped.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> o) {
        return wrapped.containsAll(o);
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public int size() {
        return wrapped.size();
    }

    @Override
    public Object[] toArray() {
        return wrapped.toArray();
    }

    @Override
    public <O> O[] toArray(O[] a) {
        return wrapped.toArray(a);
    }

    @Override
    public OdsFeatureIterator features() {
        SimpleFeatureIterator wrappedFeatures = wrapped.features();
        OdsFeatureIterator features = getFeatureIterator(wrappedFeatures);
        features.setLimit(featureCountLimit);
        return features;
    }

    protected static OdsFeatureIterator getFeatureIterator(SimpleFeatureIterator wrappedFeatures) {
        return new OdsFeatureIterator(wrappedFeatures);
    }
    
    @Override
    public SimpleFeatureCollection subCollection(Filter filter) {
        return wrapped.subCollection(filter);
    }

    @Override
    public SimpleFeatureCollection sort(SortBy order) {
        return wrapped.sort(order);
    }
}
