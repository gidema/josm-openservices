package org.openstreetmap.josm.plugins.ods;

import org.apache.commons.configuration.ConfigurationException;
import org.opengis.filter.Filter;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

/**
 * An OdsDataSource is the interface between the OdsWorkingSet and the
 * OdsFeatureSource. It performs the following tasks.
 * 
 * - Maintain a filter used when downloading features - Create a unique id for
 * each downloaded feature - Maintain a list of downloaded feature to prevent
 * duplicates
 * 
 * @author Gertjan Idema
 * 
 */
public interface OdsDataSource {
    public String getFeatureType();

    public void setRequired(boolean required);

    public boolean isRequired();

//    public ExternalEntityBuilder getEntityBuilder();

//    public FeatureMapper getFeatureMapper();

    public OdsFeatureSource getOdsFeatureSource();

    public void setFilter(Filter filter) throws ConfigurationException;

    public Filter getFilter();

    public void setIdFactory(DefaultIdFactory idFactory);

    public IdFactory getIdFactory();

    public MetaData getMetaData();

    public DownloadTask createDownloadTask(Boundary boundary);

    public void setEntityType(String entityType);

    public String getEntityType();
}
