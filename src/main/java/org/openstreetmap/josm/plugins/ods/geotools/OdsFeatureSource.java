package org.openstreetmap.josm.plugins.ods.geotools;

import java.awt.RenderingHints.Key;
import java.io.IOException;
import java.util.Set;

import org.geotools.data.DataAccess;
import org.geotools.data.FeatureListener;
import org.geotools.data.Query;
import org.geotools.data.QueryCapabilities;
import org.geotools.data.ResourceInfo;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;

/**
 * This class is a wrapper around a SimpleFeature source.
 * It returns OdsFeatureCollection in stead of SimpleFeatureCollection
 * from the getFeatures(...) methods.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdsFeatureSource<C extends OdsFeatureCollection> implements SimpleFeatureSource {
    private SimpleFeatureSource wrapped;
    private long featureCountLimit = -1;
    
    public OdsFeatureSource(SimpleFeatureSource wrapped) {
        super();
        this.wrapped = wrapped;
    }

    public void setFeatureCountLimit(long featureCountLimit) {
        this.featureCountLimit = featureCountLimit;
    }


    @Override
    public Name getName() {
        return wrapped.getName();
    }

    @Override
    public ResourceInfo getInfo() {
        return wrapped.getInfo();
    }

    @Override
    public DataAccess<SimpleFeatureType, SimpleFeature> getDataStore() {
        return wrapped.getDataStore();
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return wrapped.getQueryCapabilities();
    }

    @Override
    public void addFeatureListener(FeatureListener listener) {
        wrapped.addFeatureListener(listener);
    }

    @Override
    public void removeFeatureListener(FeatureListener listener) {
        wrapped.addFeatureListener(listener);
    }

    @Override
    public SimpleFeatureType getSchema() {
        return wrapped.getSchema();
    }

    @Override
    public ReferencedEnvelope getBounds() throws IOException {
        return wrapped.getBounds();
    }

    @Override
    public ReferencedEnvelope getBounds(Query query) throws IOException {
        return wrapped.getBounds(query);
    }

    @Override
    public int getCount(Query query) throws IOException {
        return wrapped.getCount(query);
    }

    @Override
    public Set<Key> getSupportedHints() {
        return wrapped.getSupportedHints();
    }

    @Override
    public OdsFeatureCollection getFeatures() throws IOException {
        SimpleFeatureCollection wrappedFeatureCollection = wrapped.getFeatures();
        OdsFeatureCollection featureCollection = new OdsFeatureCollection(wrappedFeatureCollection);
        featureCollection.setFeatureCountLimit(featureCountLimit);
        return featureCollection;
    }

    @Override
    public OdsFeatureCollection getFeatures(Filter filter)
            throws IOException {
        SimpleFeatureCollection wrappedFeatureCollection = wrapped.getFeatures(filter);
        OdsFeatureCollection featureCollection = new OdsFeatureCollection(wrappedFeatureCollection);
        featureCollection.setFeatureCountLimit(featureCountLimit);
        return featureCollection;
    }

    @Override
    public OdsFeatureCollection getFeatures(Query query) throws IOException {
        SimpleFeatureCollection wrappedFeatureCollection = wrapped.getFeatures(query);
        OdsFeatureCollection featureCollection = getFeatureCollection(wrappedFeatureCollection);
        featureCollection.setFeatureCountLimit(featureCountLimit);
        return featureCollection;
    }

    protected OdsFeatureCollection getFeatureCollection(SimpleFeatureCollection wrappedFeatureCollection) {
        return new OdsFeatureCollection(wrappedFeatureCollection);
    }
}
