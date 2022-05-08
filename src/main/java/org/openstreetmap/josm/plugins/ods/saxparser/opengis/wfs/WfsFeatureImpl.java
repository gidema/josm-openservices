package org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs;

import java.util.Map;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;

public class WfsFeatureImpl implements WfsFeature {
    private final QName featureType;
    private final String featureId;
    private final Geometry geometry;
    private final Map<String, String> properties;
    

    public WfsFeatureImpl(QName featureType, String feastureId, Geometry geometry, Map<String, String> properties) {
        this.featureType = featureType;
        this.featureId = feastureId;
        this.geometry = geometry;
        this.properties = properties;
    }

    @Override
    public QName getFeatureType() {
        return featureType;
    }

    @Override
    public String getFeatureId() {
        return featureId;
    }

    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }
    
    @Override
    public WfsFeature transform(CRSUtil crsUtil, Long targetSrid) {
        return new WfsFeatureImpl(featureType, featureId, crsUtil.transform(geometry, targetSrid), properties);
    }
}
