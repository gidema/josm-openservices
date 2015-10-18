package org.openstreetmap.josm.plugins.ods.geotools;

import org.opengis.feature.simple.SimpleFeature;

public interface FeatureEnhancer {
    public SimpleFeature enhance(SimpleFeature f);
}
