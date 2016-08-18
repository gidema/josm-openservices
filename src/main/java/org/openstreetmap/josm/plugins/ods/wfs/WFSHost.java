package org.openstreetmap.josm.plugins.ods.wfs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.data.wfs.protocol.wfs.Version;
import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.geotools.GtHost;

/**
 * Class to represent a WFS odsFeatureSource host.
 * 
 * @author Gertjan Idema
 * 
 */
public class WFSHost extends GtHost {

    public WFSHost(String name, String url, Integer maxFeatures) {
        super(name, url, maxFeatures);
    }

    @Override
    public Map<?, ?> getConnectionParameters() throws InitializationException {
        try {
            // TODO move to configuration phase
            // TODO add possibilities to configure parameters
            URL hostUrl = new URL(getUrl());
            URL capabilitiesUrl = WFSDataStoreFactory
                    .createGetCapabilitiesRequest(hostUrl, getWFSVersion(hostUrl));
            Map<String, Object> connectionParameters = new HashMap<>();
            connectionParameters.put(WFSDataStoreFactory.URL.key,
                    capabilitiesUrl);
            connectionParameters.put(WFSDataStoreFactory.TIMEOUT.key, 60000);
            connectionParameters.put(WFSDataStoreFactory.BUFFER_SIZE.key, 1000);
//            connectionParameters.put(WFSDataStoreFactory.PROTOCOL.key, false);
            return connectionParameters;
        } catch (MalformedURLException e) {
            throw new InitializationException(e.getMessage(), e);
        }
    }
    
    public static Version getWFSVersion(final URL host) {
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
        final Version defaultVersion = Version.v1_0_0;
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
                requestVersion = Version.find(version);
                if (requestVersion == null) {
                    requestVersion = defaultVersion;
                }
            }
        }
        return requestVersion;
    }
}
