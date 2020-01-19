package org.openstreetmap.josm.plugins.ods.wfs;

import static org.openstreetmap.josm.plugins.ods.Host.BASE_URL;
import static org.openstreetmap.josm.plugins.ods.Host.HOST_NAME;
import static org.openstreetmap.josm.plugins.ods.Host.MAX_FEATURES;
import static org.openstreetmap.josm.plugins.ods.Host.PAGE_SIZE;
import static org.openstreetmap.josm.plugins.ods.OdsDataSource.DATA_TIMEOUT;
import static org.openstreetmap.josm.plugins.ods.OdsDataSource.INIT_TIMEOUT;
import static org.openstreetmap.josm.plugins.ods.wfs.WFSHost.PROTOCOL;
import static org.openstreetmap.josm.plugins.ods.wfs.WFSHost.STRATEGY;
import static org.openstreetmap.josm.plugins.ods.wfs.WFSHost.WFS_VERSION;

import java.net.MalformedURLException;
import java.net.URL;

import org.geotools.util.Version;
import org.openstreetmap.josm.plugins.ods.ParameterSet;

public class WFSHostFactory {

    @SuppressWarnings("static-method")
    public WFSHost create(ParameterSet parameters) {
        // Required parameters
        String name = parameters.get(HOST_NAME);
        String rawUrl = parameters.get(BASE_URL);
        // Optional parameters
        Version version = parameters.get(WFS_VERSION);
        Integer maxFeatures = parameters.get(MAX_FEATURES);
        Integer pageSize = parameters.get(PAGE_SIZE);
        String strategy = parameters.get(STRATEGY);
        Boolean protocol = parameters.get(PROTOCOL);
        Integer initTimeout = parameters.get(INIT_TIMEOUT, 1000);
        Integer dataTimeout = parameters.get(DATA_TIMEOUT, 10000);
        try {
            URL url = new URL(rawUrl);
            WFSHost host = new WFSHost(name, url, version, maxFeatures, pageSize, strategy, protocol, initTimeout, dataTimeout);
            return host;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(rawUrl + " is not a valid URL");
        }
    }
}
