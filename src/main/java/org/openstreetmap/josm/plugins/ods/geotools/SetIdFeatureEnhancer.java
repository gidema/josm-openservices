package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.HashMap;
import java.util.Map;

import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.identity.FeatureId;

public class SetIdFeatureEnhancer implements FeatureEnhancer {
//    private SimpleFeatureType featureType;
    private int idAttributeIndex;
    private Map<String, Integer> index;
    
    public SetIdFeatureEnhancer(SimpleFeatureType featureType, String idAttribute) {
        this(featureType, featureType.indexOf(idAttribute));
    }
    
    public SetIdFeatureEnhancer(SimpleFeatureType featureType, int idAttribute) {
        super();
//        this.featureType = featureType;
        this.idAttributeIndex = idAttribute;
        this.index = getIndex(featureType);
    }

    @Override
    public SimpleFeature enhance(SimpleFeature feature) {
        Object fid = feature.getAttribute(idAttributeIndex);
        if (fid == null) return feature;
        Object[] values = new Object[feature.getAttributeCount()];
        for (int i=0; i<feature.getAttributeCount(); i++) {
            values[i] = feature.getAttribute(i);
        }
        FeatureId featureId = new FeatureIdImpl(fid.toString());
        return new SimpleFeatureImpl(values, feature.getFeatureType(), featureId,
            false, index);
    }
    
    private Map<String, Integer> getIndex(SimpleFeatureType featureType) {
        Map<String, Integer> index = new HashMap<String, Integer>();
        for (AttributeDescriptor descriptor : featureType.getAttributeDescriptors()) {
            String name = descriptor.getLocalName();
            Integer idx = featureType.indexOf(name);
            index.put(name,  idx);
        }
        return index;
    }
}
