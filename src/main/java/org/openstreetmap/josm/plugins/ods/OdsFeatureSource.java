package org.openstreetmap.josm.plugins.ods;

import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

/**
 * 
 * @author Gertjan Idema
 * 
 */
public interface OdsFeatureSource {
    public String getFeatureName();

    public void initialize() throws InitializationException;

    public Host getHost();
    
    public CoordinateReferenceSystem getCrs();

    public String getSRS();

    public Long getSRID();

    public FeatureType getFeatureType();

    public OdsDataSource newDataSource();

    public MetaData getMetaData();

//    public SimpleFeatureSource getFeatureSource();
}
