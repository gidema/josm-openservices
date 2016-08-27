package org.openstreetmap.josm.plugins.ods;

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
    
    /**
     * @return The name of the attribute that contains a unique Id
     *   for this feature type.
     *   Some services (WFS) return a different feature Id every time a
     *   feature is down loaded. As a consequence, we can't use the feature Id
     *   to determine if a feature was down loaded before. For those cases, we
     *   have to revert to a unique attribute as means to prevent duplicate
     *   downloads.
     */
    public String getIdAttribute();

    public MetaData getMetaData();
}
