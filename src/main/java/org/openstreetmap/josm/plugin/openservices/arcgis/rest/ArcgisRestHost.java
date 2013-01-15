package org.openstreetmap.josm.plugin.openservices.arcgis.rest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ArcgisRestHost {
  private static Map<String, ArcgisRestHost> cachedHosts =
      new HashMap<String, ArcgisRestHost>();

  private final String url;
  private String serverVersion;
  private final JSONParser parser = new JSONParser();
  private final Map<String, ArcgisRestService> services = new HashMap<String, ArcgisRestService>();
  private final List<String> folders = new LinkedList<String>();

  public ArcgisRestHost(String url) {
    super();
    this.url = url;
    try {
      init();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public String getUrl() {
    return url;
  }

  public String getServerVersion() {
    return serverVersion;
  }

  public void setServerVersion(String serverVersion) {
    this.serverVersion = serverVersion;
  }
  
  public ArcgisRestService getService(String name) throws IOException {
    ArcgisRestService service = services.get(name);
    if (service != null) {
      service.init();
    }
    return service;
  }

  private void init() throws IOException {
    try {
      JSONHttpRequest request = new JSONHttpRequest();
      request.open("GET", url.toString());
      request.addParameter("f", "json");
      request.send();
      JSONObject capabilities = request.getJson();
      parseCapabilities(capabilities);
    } catch (MalformedURLException e) {
        throw new IOException("Invalid URL", e);
    }
  }
  
  private void parseCapabilities(JSONObject capabilities) {
    setServerVersion((String) capabilities.get("currentVersion"));
    JSONArray foldersJson = (JSONArray) capabilities.get("folders");
    for (int i=0 ; i<foldersJson.size(); i++) {
      folders.add((String)foldersJson.get(i));
    }
    JSONArray servicesJson = (JSONArray) capabilities.get("services");
    for (int i=0 ; i<servicesJson.size(); i++) {
      JSONObject serviceJson = (JSONObject)servicesJson.get(i);
      String name = (String) serviceJson.get("name");
      String type = (String) serviceJson.get("type");
      services.put(name, new ArcgisRestService(this, name, type));
    }
  }
  
  public static ArcgisRestHost getHost(String hostUrl) {
    ArcgisRestHost host = cachedHosts.get(hostUrl);
    if (host == null) {
      host = new ArcgisRestHost(hostUrl);
      cachedHosts.put(hostUrl, host);
    }
    return host;
  }
}
