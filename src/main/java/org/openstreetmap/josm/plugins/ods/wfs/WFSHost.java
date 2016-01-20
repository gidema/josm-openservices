package org.openstreetmap.josm.plugins.ods.wfs;

import static org.geotools.data.wfs.WFSDataStoreFactory.BUFFER_SIZE;
import static org.geotools.data.wfs.WFSDataStoreFactory.PROTOCOL;
import static org.geotools.data.wfs.WFSDataStoreFactory.TIMEOUT;
import static org.geotools.data.wfs.WFSDataStoreFactory.URL;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.openstreetmap.josm.plugins.ods.geotools.GtHost;

import exceptions.OdsException;

/**
 * Class to represent a WFS odsFeatureSource host.
 * 
 * @author Gertjan Idema
 * 
 */
public class WFSHost extends GtHost {
    /**
     * Cache dataStores by timeout. -1 for no timeout
     */
    private Map<Integer, DataStore> dataStores = new HashMap<>();
    
    public WFSHost(String name, String urlString, Integer maxFeatures) {
        super(name, urlString, maxFeatures);
    }
    
//    @Override
//    public synchronized void initialize() throws OdsException {
//        if (isInitialized()) return;
//        super.initialize();
//    }

    /**
     * Retrieve a new DataStore for this host with the default timeout
     * 
     * @return the DataStore object
     * @throws OdsException 
     */
    public DataStore getDataStore() throws OdsException {
        return getDataStore(-1);
    }
    
    /**
     * Retrieve a new DataStore for this host with the given timeout
     * 
     * @param timeout A timeout in milliseconds
     * @return the DataStore object
     * @throws OdsException 
     */
    @Override
    public DataStore getDataStore(Integer timeout) throws OdsException {
//        assert isAvailable();
        DataStore dataStore = dataStores.get(timeout);
        if (dataStore == null) {
            Map<String, Object> connectionParameters = new HashMap<>();
            URL capabilitiesUrl = WFSDataStoreFactory
                    .createGetCapabilitiesRequest(getUrl());
            connectionParameters.put(URL.key, capabilitiesUrl);
            if (timeout > 0) {
                connectionParameters.put(TIMEOUT.key, timeout);
            }
            connectionParameters.put(BUFFER_SIZE.key, 1000);
            connectionParameters.put(PROTOCOL.key, false);
            try {
                dataStore = DataStoreFinder.getDataStore(connectionParameters);
                dataStores.put(timeout, dataStore);
            } catch (@SuppressWarnings("unused") UnknownHostException e) {
                String msg = String.format("Host %s (%s) doesn't exist",
                        getName(), getUrl().getHost());
                throw new OdsException(msg);
            } catch (@SuppressWarnings("unused") SocketTimeoutException e) {
                String msg = String.format("Host %s (%s) timed out when trying to open the datastore",
                        getName(), getUrl().toString());
                throw new OdsException(msg);
            } catch (FileNotFoundException e) {
                String msg = String.format("No dataStore for Host %s could be found at this url: %s",
                        getName(), getUrl().toString());
                throw new OdsException(msg, e);
            } catch (IOException e) {
                String msg = String.format("No dataStore for Host %s (%s) could be created",
                        getName(), getUrl().toString());
                throw new OdsException(msg, e);
            }
        }
        return dataStore;
    }
}
