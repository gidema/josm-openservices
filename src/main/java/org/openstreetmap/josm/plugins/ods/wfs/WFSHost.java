package org.openstreetmap.josm.plugins.ods.wfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.geotools.data.DataStore;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.data.wfs.WFSServiceInfo;
import org.geotools.data.wfs.impl.WFSDataAccessFactory;
import org.geotools.data.wfs.internal.Versions;
import org.geotools.data.wfs.internal.WFSGetCapabilities;
import org.geotools.util.Version;
import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.geotools.GtHost;
import org.openstreetmap.josm.tools.I18n;

import net.opengis.ows11.CapabilitiesBaseType;
import net.opengis.ows11.DomainType;
import net.opengis.ows11.OperationType;
import net.opengis.ows11.OperationsMetadataType;
import net.opengis.ows11.ValueType;

/**
 * Class to represent a WFS odsFeatureSource host.
 *
 * TODO Remove dependency on GtHost
 *
 * @author Gertjan Idema
 *
 */
public class WFSHost extends GtHost {
    private static WFSDataStoreFactory wfsDataStoreFactory = new WFSDataStoreFactory();

    private DataStore dataStore;
    private final int initTimeout;
    private final int dataTimeout;
    private final Set<String> featureTypes = new HashSet<>();
    private boolean pagingSupported = false;
    private int defaultPageSize = 0;

    public WFSHost(String name, String url, Integer maxFeatures, int initTimeout, int dataTimeout) {
        super(name, url, maxFeatures);
        this.initTimeout = initTimeout;
        this.dataTimeout = dataTimeout;
    }

    public boolean isPagingSupported() {
        return pagingSupported;
    }

    public int getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    @Override
    public synchronized void initialize() throws InitializationException {
        if (!isInitialized()) {
            super.initialize();
            setInitialized(false);
            try {
                dataStore = createDataStore(initTimeout);
            } catch (IOException e) {
                throw new InitializationException(e);
            }
            try {
                dataStore = createDataStore(dataTimeout);
                featureTypes.addAll(Arrays.asList(dataStore.getTypeNames()));
            } catch (IOException e) {
                throw new InitializationException(e);
            }
            setInitialized(true);
        }
        return;
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
    public Map<String, Serializable> getConnectionParameters() throws IOException {
        // TODO move to configuration phase
        // TODO add possibilities to configure parameters
        URL capabilitiesUrl = WFSDataStoreFactory
                .createGetCapabilitiesRequest(getUrl(), getWFSVersion(getUrl()));
        Map<String, Serializable> connectionParameters = new HashMap<>();
        connectionParameters.put(WFSDataAccessFactory.URL.key, capabilitiesUrl);
        connectionParameters.put(WFSDataAccessFactory.TIMEOUT.key, 60000);
        connectionParameters.put(WFSDataAccessFactory.BUFFER_SIZE.key, 1000);
        //            connectionParameters.put(WFSDataStoreFactory.PROTOCOL.key, false);
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
        Map<String, Serializable> connectionParameters = getConnectionParameters();
        WFSDataStore ds;
        try {
            ds = wfsDataStoreFactory.createDataStore(connectionParameters);
            if (ds == null) {
                throw new IOException("No data store could be found");
            }
            WFSServiceInfo serviceInfo = ds.getInfo();
            switch (serviceInfo.getVersion()) {
            case "1.0.0":
            case "1.1.0":
                break;
            case "2.0.0":
                WFSGetCapabilities wfsCapabilities = ds.getWfsClient().getCapabilities();
                processWFSCapabilities(wfsCapabilities);
            }
        } catch (SocketException|SocketTimeoutException e) {
            String msg = I18n.tr("Host {0} ({1}) timed out when trying to open the datastore",
                    getName(), getUrl().toString());
            throw new IOException(msg);
        } catch (FileNotFoundException e) {
            String msg = I18n.tr("No dataStorowe for Host {0} could be found at this url: {1}",
                    getName(), getUrl().toString());
            throw new IOException(msg);
        }
        return ds;
    }

    private void processWFSCapabilities(WFSGetCapabilities wfsCapabilities) {
        CapabilitiesBaseType capabilitiesType = (CapabilitiesBaseType) wfsCapabilities.getParsedCapabilities();
        OperationsMetadataType metaData = capabilitiesType.getOperationsMetadata();
        processConstraints(metaData.getConstraint());
        processOperations(metaData.getOperation());
    }

    private void processConstraints(EList<?> constraints) {
        @SuppressWarnings("unchecked")
        Iterator<DomainType> it = (Iterator<DomainType>) constraints.iterator();
        while (it.hasNext()) {
            DomainType domainType = it.next();
            ValueType valueType = domainType.getDefaultValue();
            String sDefault = (valueType == null ? null :valueType.getValue());
            switch (domainType.getName()) {
            case "ImplementsResultPaging":
                pagingSupported = Boolean.parseBoolean(sDefault);
                break;
            }
        }
    }

    private void processOperations(EList<?> operations) {
        @SuppressWarnings("unchecked")
        Iterator<OperationType> it = (Iterator<OperationType>) operations.iterator();
        while (it.hasNext()) {
            OperationType operation = it.next();
            switch (operation.getName()) {
            case "GetFeature":
                processGetFeatureConstraints(operation.getConstraint());
                break;
            }
        }
    }

    private void processGetFeatureConstraints(EList<?> constraints) {
        @SuppressWarnings("unchecked")
        Iterator<DomainType> it = (Iterator<DomainType>) constraints.iterator();
        while (it.hasNext()) {
            DomainType constraint = it.next();
            switch (constraint.getName()) {
            case "CountDefault":
                String sValue = constraint.getDefaultValue().getValue();
                this.setDefaultPageSize(Integer.parseInt(sValue));
            }
        }
    }
}
