package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;

public class UniqueKeyFilterFactory implements FilterFactory {
    final String keyProperty;
    final String[] keyProperties;

    public UniqueKeyFilterFactory(String property) {
        this.keyProperty = property;
        this.keyProperties = null;
    }
    
    public UniqueKeyFilterFactory(List<String> properties) {
        if (properties.size() == 1) {
            this.keyProperty = properties.get(0);
            this.keyProperties = null;
        }
        else {
            this.keyProperty = null;
            this.keyProperties = properties.toArray(new String[0]);
        }
    }
    
    boolean isSimple() {
        return keyProperty != null; 
    }
    
    @Override
    public FilteringFeatureVisitor instance() {
        return new UniqueKeyFilter();
    }

    class UniqueKeyFilter implements FilteringFeatureVisitor {
        private FeatureVisitor consumer;
        private final Set<Object> keys = new HashSet<>();
        
        @Override
        public void setConsumer(FeatureVisitor consumer) {
            this.consumer = consumer;
        }

        @Override
        public void visit(Feature feature) {
            Object key = getKey(feature);
            if (keys.add(key)) {
                consumer.visit(feature);
            }
        }
        
        private Object getKey(Feature feature) {
            if (isSimple()) {
                return feature.getProperty(keyProperty).getValue();
            }
            List<Object> key = new ArrayList<>(keyProperties.length);
            for (int i=0; i<keyProperties.length; i++) {
                key.add(feature.getProperty(keyProperties[i]).getValue());
            }
            return key;
        }
    }
}
