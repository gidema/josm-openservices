package org.openstreetmap.josm.plugins.openservices;

import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaData;

/**
 * 
 * @author Gertjan Idema
 * 
 */
public interface OdsFeatureSource {
    public String getFeatureName();

    public void initialize() throws InitializationException;

    public CoordinateReferenceSystem getCrs();

    public String getSRS();

    public Long getSRID();

    public FeatureType getFeatureType();

    public OdsDataSource newDataSource();

    public MetaData getMetaData();
}
