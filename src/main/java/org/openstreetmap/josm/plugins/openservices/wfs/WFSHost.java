package org.openstreetmap.josm.plugins.openservices.wfs;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.openstreetmap.josm.plugins.openservices.Host;
import org.openstreetmap.josm.plugins.openservices.Service;
import org.openstreetmap.josm.plugins.openservices.ServiceException;

/**
 * Class to represent a WFS service host.
 * @author Gertjan Idema
 *
 */
public class WFSHost extends Host {
  private final WFSDataStoreFactory dataStoreFactory = new WFSDataStoreFactory();
  private boolean initialized = false;
  private DataStore dataStore;
  private List<String> featureTypes;
  
  /**
   * Initialize the WFS host. This initializes the dataStoreFactory
   * Must be synchronized.
   * @throws WfsException
   */
  @Override
  public synchronized void init() throws WfsException {
    if (initialized) return;
    initialize();
  }
  
  private void initialize() throws WfsException {
    URL hostUrl = null;
    try {
      hostUrl = new URL(getUrl());
    } catch (MalformedURLException e) {
      throw new WfsException(e.getMessage(), e);
    }
    URL capabilitiesUrl = WFSDataStoreFactory.createGetCapabilitiesRequest(hostUrl);
    Map<String, Object> connectionParameters = new HashMap<String, Object>();
    connectionParameters.put(WFSDataStoreFactory.URL.key, capabilitiesUrl);
    try {
      dataStore = dataStoreFactory.createDataStore(connectionParameters);
    } catch (ConnectException e) {
      if (e.getMessage().equals("Connection refused")) {
        throw new WfsException("Connection refused; Make sure the wfs service is running");
      }
      throw new WfsException(e);
    } catch (IOException e) {
      throw new WfsException(e);
    }
    try {
      featureTypes = Arrays.asList(dataStore.getTypeNames());
    } catch (IOException e) {
      throw new WfsException("Unable to retreive features from service");
    }
    initialized = true;
  }

  /**
   * @return the DataStore object
   * @throws WfsException 
   */
  public DataStore getDataStore() throws WfsException {
    init();
    return dataStore;
  }
  
  @Override
  public boolean hasFeatureType(String type) throws ServiceException {
    init();
    return featureTypes.contains(type);
  }

  @Override
  public Service getService(String feature) {
    WFSService service = new WFSService();
    service.setHost(this);
    service.setFeature(feature);
    return service;
  }
}
