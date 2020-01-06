package org.openstreetmap.josm.plugins.ods.entities.opendata;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;

public class FeatureUtil {
    public static Double getDouble(SimpleFeature feature, String name ) {
        Property property = feature.getProperty(name);
        if (property == null) return null;
        return (Double) property.getValue();
    }

    public static BigDecimal getBigDecimal(SimpleFeature feature, String name ) {
        Property property = feature.getProperty(name);
        if (property == null || property.getValue() == null) return null;
        return (BigDecimal) property.getValue();
    }

    public static BigInteger getBigInteger(SimpleFeature feature, String name) {
        Property property = feature.getProperty(name);
        if (property == null || property.getValue() == null) return null;
        return (BigInteger) property.getValue();
    }

    public static String getString(SimpleFeature feature, String name ) {
        Property property = feature.getProperty(name);
        if (property == null) return null;
        String s = (String) property.getValue();
        return (s == null || s.isEmpty()) ? null : s;
    }
    
    public static Character getCharacter(SimpleFeature feature, String name) {
        String s = getString(feature, name);
        return (s == null || s.isEmpty()) ? null : s.charAt(0);
    }
    
    public static Optional<String> getOptionalString(SimpleFeature feature, String name ) {
        Property property = feature.getProperty(name);
        if (property == null  || property.getValue() == null) return Optional.empty();
        return Optional.of((String) property.getValue());
    }
    
    public static Integer getInteger(SimpleFeature feature, String name ) {
        Property property = feature.getProperty(name);
        if (property == null || property.getValue() == null) return null;
        Number number = (Number) property.getValue();
        return number.intValue();
    }
    
    public static Long getLong(SimpleFeature feature, String name ) {
        Property property = feature.getProperty(name);
        if (property == null || property.getValue() == null) return null;
        Number number = (Number) property.getValue();
        return number.longValue();
    }
}
