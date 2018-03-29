package org.openstreetmap.josm.plugins.ods.wfs;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.data.wfs.impl.WFSDataAccessFactory;
import org.geotools.data.wfs.internal.Versions;
import org.geotools.util.Version;
import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.geotools.GtHost;
import org.openstreetmap.josm.tools.I18n;

/**
 * Class to represent a WFS odsFeatureSource host.
 * 
 * TODO Remove dependency on GtHost
 * 
 * @author Gertjan Idema
 * 
 */
public class WFSHost extends GtHost {
    private DataStore dataStore;
    private Set<String> featureTypes = new HashSet<>();

/**    static {
        // Hack to fix issues with geotools wfs-ng
        // 
        WFSExtensions.findResponseFactories(WFSOperationType.DESCRIBE_FEATURETYPE);
    }*/
    public WFSHost(String name, String url, Integer maxFeatures) {
        super(name, url, maxFeatures);
    }

    @Override
    public synchronized void initialize() throws InitializationException {
        if (isInitialized()) return;
        super.initialize();
        setInitialized(false);
        
        // TODO move next line to configuration phase
        Map<String, Serializable> connectionParameters = getConnectionParameters();
        try {
            WFSDataStoreFactory factory = new WFSDataStoreFactory();
            dataStore = factory.createDataStore(connectionParameters);
            if (dataStore == null) {
                throw new InitializationException(I18n.tr("Could not find an appropriate data store for these connection parameters:\n{0}",
                        connectionParameters.toString()));
            }
            featureTypes = new HashSet<>(Arrays.asList(getDataStore().getTypeNames()));
            setInitialized(true);
        } catch (IOException e) {
            throw new InitializationException(
                    "Unable to connect to the datastore", e);
        }
    }
    
    @Override
    public DataStore getDataStore() {
        return dataStore;
    }
    
    @Override
    protected Set<String> getFeatureTypes() {
        return featureTypes;
    }

    @Override
    public Map<String, Serializable> getConnectionParameters() throws InitializationException {
        try {
            // TODO move to configuration phase
            // TODO add possibilities to configure parameters
            URL hostUrl = new URL(getUrl());
            URL capabilitiesUrl = WFSDataStoreFactory
                    .createGetCapabilitiesRequest(hostUrl, getWFSVersion(hostUrl));
            Map<String, Serializable> connectionParameters = new HashMap<>();
            connectionParameters.put(WFSDataAccessFactory.URL.key,
                    capabilitiesUrl);
            connectionParameters.put(WFSDataAccessFactory.TIMEOUT.key, 60000);
            connectionParameters.put(WFSDataAccessFactory.BUFFER_SIZE.key, 1000);
//            connectionParameters.put(WFSDataStoreFactory.PROTOCOL.key, false);
            return connectionParameters;
        } catch (MalformedURLException e) {
            throw new InitializationException(e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("static-method")
    private Version getWFSVersion(final URL host) {
        if (host == null) {
            throw new NullPointerException("url");
        }

        String queryString = host.getQuery();
        queryString = queryString == null || "".equals(queryString.trim()) ? "" : queryString
                .toUpperCase();

        // final Version defaultVersion = Version.highest();
        
        // We cannot use the highest vesion as the default yet
        // since v1_1_0 does not implement a read/write datastore
        // and is still having trouble with requests from
        // different projections etc...
        //
        // this is a result of the udig code sprint QA run
        final Version defaultVersion = Versions.v1_0_0;
        // which version to use
        Version requestVersion = defaultVersion;

        if (queryString.length() > 0) {

            Map<String, String> params = new HashMap<>();
            String[] split = queryString.split("&");
            for (String kvp : split) {
                int index = kvp.indexOf('=');
                String key = index > 0 ? kvp.substring(0, index) : kvp;
                String value = index > 0 ? kvp.substring(index + 1) : null;
                params.put(key, value);
            }

            String version = params.get("VERSION");
            if (version != null) {
                requestVersion = new Version(version);
            }
        }
        return requestVersion;
    }
}
