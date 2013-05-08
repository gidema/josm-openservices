package org.openstreetmap.josm.plugins.openservices.wfs;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.openstreetmap.josm.plugins.openservices.geotools.GtException;
import org.openstreetmap.josm.plugins.openservices.geotools.GtHost;

/**
 * Class to represent a WFS service host.
 * @author Gertjan Idema
 *
 */
public class WFSHost extends GtHost {
  private final WFSDataStoreFactory dataStoreFactory = new WFSDataStoreFactory();
  private DataStore dataStore;  

  /**
   * @return the DataStore object
   * @throws GtException 
   */
  @Override
  public DataStore getDataStore() throws GtException {
    if (dataStore == null) {
      try {
        URL hostUrl = new URL(getUrl());
        URL capabilitiesUrl = WFSDataStoreFactory.createGetCapabilitiesRequest(hostUrl);
        Map<String, Object> connectionParameters = new HashMap<String, Object>();
        connectionParameters.put(WFSDataStoreFactory.URL.key, capabilitiesUrl);
        dataStore = dataStoreFactory.createDataStore(connectionParameters);
      } catch (ConnectException e) {
        if (e.getMessage().equals("Connection refused")) {
          // TODO Show proper error message to the user 
          throw new GtException("Connection refused; Make sure the wfs service is running");
        }
        throw new GtException(e);
      } catch (IOException e) {
        throw new GtException(e);
      }
    }
    return dataStore;
  }
}
