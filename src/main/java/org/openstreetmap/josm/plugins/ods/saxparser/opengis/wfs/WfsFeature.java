package org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;

public interface WfsFeature {
    public String getFeatureId();
    public QName getFeatureType();
    public Geometry getGeometry();
    public String getProperty(String key);
    public WfsFeature transform(CRSUtil crsUtil, Long targetSrid);
}
