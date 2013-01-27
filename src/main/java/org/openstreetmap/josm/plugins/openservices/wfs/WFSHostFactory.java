package org.openstreetmap.josm.plugins.openservices.wfs;

import java.util.HashMap;
import java.util.Map;

public class WFSHostFactory {
  private static Map<String, WfsHost> repository = new HashMap<String, WfsHost>();

  public static WfsHost get(String url) throws WfsException {
    WfsHost host = repository.get(url);
    if (host == null) {
      host = new WfsHost(url);
      host.init();
      repository.put(url, host);
    }
    return host;
  }
}
