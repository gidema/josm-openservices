package org.openstreetmap.josm.plugins.ods.geotools.impl;

import java.io.IOException;
import java.util.Collection;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.util.ProgressListener;
import org.openstreetmap.josm.plugins.ods.geotools.GtPageReader;

/**
 * Default implementation of the GtPageReader class.
 * 
 * @See GtPageReader
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class DefaultGtPageReader implements GtPageReader {
    private SimpleFeatureSource featureSource;
    
    public DefaultGtPageReader(SimpleFeatureSource featureSource) {
        super();
        this.featureSource = featureSource;
    }

    @Override
    public Collection<SimpleFeature> read(Query query, ProgressListener progressListener) throws IOException {
        SimpleFeatureCollection featureCollection = featureSource.getFeatures(query);
        Visitor visitor = new Visitor();
        featureCollection.accepts(visitor, progressListener);
        return visitor.getFeatures();
    }
    
    class Visitor implements FeatureVisitor {
        private DefaultFeatureCollection features;
        int featureCount = 0;
        
        public Visitor() {
            super();
            this.features = new DefaultFeatureCollection();
        }

        @Override
        public void visit(Feature feature) {
            features.add((SimpleFeature)feature);
            featureCount++;
        }

        public int getFeatureCount() {
            return featureCount;
        }

        public DefaultFeatureCollection getFeatures() {
            return features;
        }
    }
}
