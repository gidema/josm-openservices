package org.openstreetmap.josm.plugins.ods.wfs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.wfs.WFSDataStoreFactory;
import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.geotools.GtHost;

import com.google.inject.Inject;

/**
 * Class to represent a WFS odsFeatureSource host.
 * 
 * @author Gertjan Idema
 * 
 */
public class WFSHost extends GtHost {

    @Inject
    public WFSHost(String name, String url, Integer maxFeatures) {
        super(name, url, maxFeatures);
    }

    @Override
    public Map<?, ?> getConnectionParameters() throws InitializationException {
        try {
            // TODO move to configuration fase
            // TODO add possibilities to configure parameters
            URL hostUrl = new URL(getUrl());
            URL capabilitiesUrl = WFSDataStoreFactory
                    .createGetCapabilitiesRequest(hostUrl);
            Map<String, Object> connectionParameters = new HashMap<>();
            connectionParameters.put(WFSDataStoreFactory.URL.key,
                    capabilitiesUrl);
            connectionParameters.put(WFSDataStoreFactory.TIMEOUT.key, 60000);
            connectionParameters.put(WFSDataStoreFactory.BUFFER_SIZE.key, 1000);
            connectionParameters.put(WFSDataStoreFactory.PROTOCOL.key, "FALSE");
            return connectionParameters;
        } catch (MalformedURLException e) {
            throw new InitializationException(e.getMessage(), e);
        }
    }
}
