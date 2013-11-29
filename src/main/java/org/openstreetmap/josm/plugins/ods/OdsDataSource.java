package org.openstreetmap.josm.plugins.ods;

import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.opengis.filter.Filter;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.imported.ImportedEntityBuilder;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.tags.FeatureMapper;

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

    public ImportedEntityBuilder getEntityBuilder();

    public FeatureMapper getFeatureMapper();

    public OdsFeatureSource getOdsFeatureSource();

    public void setFilter(Filter filter) throws ConfigurationException;

    public Filter getFilter();

    public void setIdFactory(DefaultIdFactory idFactory);

    public IdFactory getIdFactory();

    public MetaData getMetaData();

    public DownloadJob createDownloadJob(ImportDataLayer dataLayer,
            Bounds bounds, Set<Entity> newEntities);

    void addFeatureListener(FeatureListener featureListener);

    void setEntityClass(Class<? extends Entity> entityClass);

}
