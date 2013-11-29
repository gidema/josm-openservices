package org.openstreetmap.josm.plugins.ods.wfs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.wfs.WFSDataStoreFactory;
import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.geotools.GtHost;

/**
 * Class to represent a WFS odsFeatureSource host.
 * @author Gertjan Idema
 *
 */
public class WFSHost extends GtHost {

  @Override
  public Map<?, ?> getConnectionParameters() throws InitializationException{
    try {
      // TODO move to configuration fase
      URL hostUrl = new URL(getUrl());
      URL capabilitiesUrl = WFSDataStoreFactory.createGetCapabilitiesRequest(hostUrl);
      Map<String, Object> connectionParameters = new HashMap<String, Object>();
      connectionParameters.put(WFSDataStoreFactory.URL.key, capabilitiesUrl);
      return connectionParameters;
    } catch (MalformedURLException e) {
      throw new InitializationException(e.getMessage(), e);
    }
  }
}
