package org.openstreetmap.josm.plugins.ods.io;

import java.net.URL;

import org.openstreetmap.josm.plugins.ods.OdsConfigurationException;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.ServiceException;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

import exceptions.OdsException;

/**
 * Host for 1 or more data services
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface Host {

    String getName();

    URL getUrl();

    String getType();

    /**
     * @return The maximum number of features the services of this host will return for
     *     one request. -1 if the number is unlimited
     */
    Integer getMaxFeatures();

    /**
     * @return Meta data for this host.
     */
    MetaData getMetaData();

    /**
     * Perform any initialisation required for this host.
     * Typically, this included building a list of available features.
     * 
     * Do nothing if the host has already been initialised.
     * @throws OdsConfigurationException 
     */
    void initialize() throws OdsException;

    /**
     * @return true if the server is available
     */
    boolean isAvailable();

    /**
     * @param feature The requested feature
     * @return true if this host provides a feature with the specified name.
     * @throws ServiceException
     */
    boolean hasFeatureType(String feature) throws ServiceException;

    /**
     * @param feature
     * @return A OdsFeatureSource object for the feature with the specified name
     * @throws ServiceException
     */
    OdsFeatureSource getOdsFeatureSource(String feature)
            throws ServiceException;

}