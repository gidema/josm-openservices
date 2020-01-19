package org.openstreetmap.josm.plugins.ods.wfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.data.wfs.impl.WFSDataAccessFactory;
import org.geotools.data.wfs.internal.Versions;
import org.geotools.util.Version;
import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.ParameterType;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtHost;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.tools.I18n;

//import net.opengis.ows11.CapabilitiesBaseType;
//import net.opengis.ows11.DomainType;
//import net.opengis.ows11.OperationType;
//import net.opengis.ows11.OperationsMetadataType;
//import net.opengis.ows11.ValueType;

/**
 * Class to represent a WFS odsFeatureSource host.
 *
 * TODO Remove dependency on GtHost
 *
 * @author Gertjan Idema
 *
 */
public class WFSHost implements GtHost {
    public final static ParameterType<Version> WFS_VERSION;
    public final static ParameterType<String> STRATEGY;
    public final static ParameterType<Boolean> PROTOCOL;

    static
    {
        WFS_VERSION = new ParameterType<>(Version.class);
        STRATEGY = ParameterType.STRING();
        PROTOCOL = ParameterType.BOOLEAN();
    }

    private static WFSDataStoreFactory wfsDataStoreFactory = new WFSDataStoreFactory();
    private final static String TYPE = "WFS";

    private final String name;
    private final URL url;
    private Version wfsVersion;
    private final Integer maxFeatures;
    private final Integer defaultPageSize;
    private final String strategy;
    private final Boolean protocol;

    private final Integer initTimeout;
    private final Integer dataTimeout;
    private Boolean initialized = false;
    //    private boolean pagingSupported = false;

    public WFSHost(String name, URL url, Version wfsVersion,
            Integer maxFeatures, Integer defaultPageSize, String strategy,
            Boolean protocol, Integer initTimeout, Integer dataTimeout) {
        super();
        this.name = name;
        this.url = url;
        this.wfsVersion = wfsVersion;
        this.maxFeatures = maxFeatures;
        this.defaultPageSize = defaultPageSize;
        this.strategy = strategy;
        this.protocol = protocol;
        this.initTimeout = initTimeout;
        this.dataTimeout = dataTimeout;
    }

    //    public boolean isPagingSupported() {
    //        return pagingSupported;
    //    }
    //
    //    public int getDefaultPageSize() {
    //        return defaultPageSize;
    //    }
    //
    //    // TODO: Deprecate. Do this in a factory class
    //    public void setDefaultPageSize(int defaultPageSize) {
    ////        this.pagingCapabilities.setDefaultPageSize(defaultPageSize);
    //    }


    //    public boolean isPagingSupported() {
    //        return pagingSupported;
    //    }
    //
    //    public int getDefaultPageSize() {
    //        return defaultPageSize;
    //    }
    //
    //    // TODO: Deprecate. Do this in a factory class
    //    public void setDefaultPageSize(int defaultPageSize) {
    ////        this.pagingCapabilities.setDefaultPageSize(defaultPageSize);
    //    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Integer getMaxFeatures() {
        return maxFeatures;
    }

    @Override
    public MetaData getMetaData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public OdsFeatureSource getOdsFeatureSource(String feature) {
        return new GtFeatureSource(this, feature, null);
    }

    @Override
    public synchronized void initialize() throws InitializationException {
        try {
            if (!isInitialized()) {
                if (wfsVersion == null) {
                    // Check the WFS version that is negotiated with the server
                    // TODO
                    WFSDataStore dataStore = createDataStore(initTimeout);
                    wfsVersion = new Version(dataStore.getInfo().getVersion());
                }
                this.initialized = true;
            }
        } catch (IOException e) {
            throw new InitializationException(e);
        }
        return;
    }

    @Override
    public DataStore createDataStore() throws IOException {
        return createDataStore(dataTimeout);
    }

    public Map<String, Serializable> getConnectionParameters(int timeOut) {
        URL capabilitiesUrl = WFSDataStoreFactory
                .createGetCapabilitiesRequest(getUrl(), wfsVersion);
        Map<String, Serializable> connectionParameters = new HashMap<>();
        connectionParameters.put(WFSDataAccessFactory.URL.key, capabilitiesUrl);
        connectionParameters.put(WFSDataAccessFactory.TIMEOUT.key, timeOut);
        if (protocol != null) {
            connectionParameters.put(WFSDataAccessFactory.PROTOCOL.key, protocol);
        }
        if (strategy != null) {
            connectionParameters.put(WFSDataAccessFactory.WFS_STRATEGY.key, strategy);
        }
        return connectionParameters;
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

    /**
     * Create a new DataStore for this host with the given timeout
     *
     * @param timeout A timeout in milliseconds
     * @return the DataStore object
     * @throws OdsException
     */
    private WFSDataStore createDataStore(Integer timeout) throws IOException {
        Map<String, Serializable> connectionParameters = getConnectionParameters(timeout);
        WFSDataStore dataStore;
        try {
            dataStore = wfsDataStoreFactory.createDataStore(connectionParameters);
            if (dataStore == null) {
                throw new IOException("No data store could be found");
            }
            return dataStore;
        } catch (SocketException|SocketTimeoutException e) {
            String msg = I18n.tr("Host {0} ({1}) timed out when trying to open the datastore",
                    getName(), getUrl().toString());
            throw new IOException(msg);
        } catch (FileNotFoundException e) {
            String msg = I18n.tr("No dataStore for Host {0} could be found at this url: {1}",
                    getName(), getUrl().toString());
            throw new IOException(msg);
        }
    }
}
