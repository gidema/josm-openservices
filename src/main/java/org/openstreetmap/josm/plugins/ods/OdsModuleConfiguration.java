package org.openstreetmap.josm.plugins.ods;

import java.util.Collection;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.io.Host;

import exceptions.OdsException;

/**
 * Provide the configuration for an Ods Module.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface OdsModuleConfiguration {

    /**
     * @return A collection of (uninitialized) hosts required by this module. The list will
     * be used to initialize the hosts when the module is activated
     */
    Collection<Host> getHosts();

    /**
     * @return A list of (uninitialized) featureSources required by this module. The list will
     * be used to initialize the featureSources when the module is activated
     */
    List<? extends OdsFeatureSource> getFeatureSources();

    /**
     * @return A collection of (uninitialized) dataSources required by this module. The list will
     * be used to initialize the dataSources when the module is activated
     */
    Collection<OdsDataSource> getDataSources();

    OdsDataSource getDataSource(String name) throws OdsException;

}