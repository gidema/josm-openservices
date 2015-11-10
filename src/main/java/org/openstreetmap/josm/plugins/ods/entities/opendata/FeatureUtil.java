package org.openstreetmap.josm.plugins.ods.entities.opendata;

import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;

public class FeatureUtil {
    public static Double getDouble(SimpleFeature feature, String name ) {
        Property property = feature.getProperty(name);
        if (property == null) return null;
        return (Double) property.getValue();
    }

    public static String getString(SimpleFeature feature, String name ) {
        Property property = feature.getProperty(name);
        if (property == null) return null;
        return (String) property.getValue();
    }
    
    public static Integer getInteger(SimpleFeature feature, String name ) {
        Property property = feature.getProperty(name);
        if (property == null) return null;
        Number number = (Number) property.getValue();
        if (number == null) return null;
        return number.intValue();
    }
    
    public static Long getLong(SimpleFeature feature, String name ) {
        Property property = feature.getProperty(name);
        if (property == null) return null;
        Number number = (Number) property.getValue();
        if (number == null) return null;
        return number.longValue();
    }
}
