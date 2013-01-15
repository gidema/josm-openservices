package org.openstreetmap.josm.plugins.openservices.wfs;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;

/**
 * Class to represent a WFS service host.
 * @author Gertjan Idema
 *
 */
public class WfsHost {
  private final WFSDataStoreFactory dataStoreFactory = new WFSDataStoreFactory();
  private boolean initialized = false;
  private final String uri;
  private DataStore dataStore;
  private final List<WfsFeature> features = new LinkedList<WfsFeature>();
  
  /**
   * Create a new WFS host for the given uri.
   * @param uri
   */
  public WfsHost(String uri) {
    this.uri = uri;
  }
  
  protected void addFeature(String featureName) {
    features.add(new WfsFeature(this, featureName));
  }
  
  /**
   * Initialize the WFS host. This initializes de dataStoreFactory
   * @throws WfsException
   */
  public void init() throws WfsException {
    if (initialized) return;
    URL url = null;
    try {
      url = new URL(uri);
    } catch (MalformedURLException e) {
      throw new WfsException(e.getMessage(), e);
    }
    URL capabilitiesUrl = WFSDataStoreFactory.createGetCapabilitiesRequest(url);
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
    initialized = true;
  }

  /**
   * @return the DataStore object
   */
  public DataStore getDataStore() {
    return dataStore;
  }
}
