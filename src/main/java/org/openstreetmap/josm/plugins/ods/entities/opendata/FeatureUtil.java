package org.openstreetmap.josm.plugins.ods.entities.opendata;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;

public class FeatureUtil {
    public static Double getDouble(WfsFeature feature, String name ) {
        String value = feature.getProperty(name);
        return value == null ? null :Double.valueOf(feature.getProperty(name));
    }

    public static BigDecimal getBigDecimal(WfsFeature feature, String name ) {
        String value = feature.getProperty(name);
        return value == null ? null : new BigDecimal(feature.getProperty(name));
    }

    public static BigInteger getBigInteger(WfsFeature feature, String name) {
        String value = feature.getProperty(name);
        return value == null ? null : new BigInteger(feature.getProperty(name));
    }

    public static String getString(WfsFeature feature, String name ) {
        String value = feature.getProperty(name);
        return (value == null || value.isEmpty()) ? null : value;
    }
    
    public static Character getCharacter(WfsFeature feature, String name) {
        String s = getString(feature, name);
        return (s == null) ? null : s.charAt(0);
    }
    
    public static Optional<String> getOptionalString(WfsFeature feature, String name ) {
        return Optional.ofNullable(getString(feature, name));
    }
    
    public static Integer getInteger(WfsFeature feature, String name ) {
        String value = getString(feature, name);
        return value == null ? null : Integer.valueOf(value);
    }
    
    public static Long getLong(WfsFeature feature, String name ) {
        String value = getString(feature, name);
        return value == null ? null : Long.valueOf(value);
    }
}
