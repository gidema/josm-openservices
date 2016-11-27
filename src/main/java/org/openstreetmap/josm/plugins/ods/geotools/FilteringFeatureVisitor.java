package org.openstreetmap.josm.plugins.ods.geotools;

import org.opengis.feature.FeatureVisitor;

public interface FilteringFeatureVisitor extends FeatureVisitor {
    void setConsumer(FeatureVisitor consumer);
}
