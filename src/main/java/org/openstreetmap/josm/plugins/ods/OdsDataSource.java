package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.plugins.ods.geotools.GtQuery;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

/**
 * <p>An OdsDataSource is the interface between the OdsModule and the
 * OdsFeatureSource. It performs the following tasks.</p>
 * <ul>
 * <li>Maintain a filter used when downloading features</li>
 * <li>Create a unique id for each downloaded feature</li>
 * <li>Maintain a list of downloaded feature to prevent duplicates</li>
 *
 * @author Gertjan Idema
 *
 */
public interface OdsDataSource {
    public String getFeatureType();

    public OdsFeatureSource getOdsFeatureSource();

    public GtQuery getQuery();

    public MetaData getMetaData();

    public void setRequired(boolean required);

    public boolean isRequired();
}
