package org.openstreetmap.josm.plugins.ods;

import javax.xml.namespace.QName;

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
    // Time-out used when initializing a dataSource
    public static ParameterType<Integer> INIT_TIMEOUT = new ParameterType<>(Integer.class);
    // Time-out used when retrieving data from a dataSource
    public static ParameterType<Integer> DATA_TIMEOUT = new ParameterType<>(Integer.class);

    public QName getFeatureType();

    public OdsFeatureSource getOdsFeatureSource();

    public MetaData getMetaData();

    public void setRequired(boolean required);

    public boolean isRequired();

    /**
     * Get the desired page size. 0 means no paging.
     *
     * @return the page size
     */
    int getPageSize();
}
