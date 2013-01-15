package org.openstreetmap.josm.plugin.openservices.arcgis.rest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ArcgisRestService {
  private ArcgisRestHost host;
  private final String name;
  private final String type;
  private String serviceDescription;
  private String description;
  private String copyrightText;
  private boolean initialized;
  
  private final Map<Long, ArcgisRestLayer> layers = new HashMap<Long, ArcgisRestLayer>();

  public ArcgisRestService(ArcgisRestHost host, String name, String type) {
    super();
    this.host = host;
    this.name = name;
    this.type = type;
  }

  public ArcgisRestHost getHost() {
    return host;
  }

  public void setHost(ArcgisRestHost host) {
    this.host = host;
  }

  public ArcgisRestLayer getLayer(Long id) throws IOException {
    ArcgisRestLayer layer = layers.get(id);
    if (layer != null) {
      layer.init();
    }
    return layer;
  }

  public String getServiceDescription() {
    return serviceDescription;
  }

  public String getDescription() {
    return description;
  }

  public String getCopyrightText() {
    return copyrightText;
  }

  public String getBaseUrl() {
    return host.getUrl() + "/" + name + "/" + type;
  }
  
  public void init() throws IOException {
    if (initialized) {
      return;
    }
    try {
      JSONHttpRequest request = new JSONHttpRequest();
      request.open("GET", getBaseUrl());
      request.addParameter("f", "json");
      request.send();
      JSONObject capabilities = request.getJson();
      parseMetaData(capabilities);
      initialized = true;
    } catch (MalformedURLException e) {
        throw new RuntimeException(e.getMessage());
    }
  }
  
  private void parseMetaData(JSONObject capabilities) {
    serviceDescription = (String) capabilities.get("serviceDescription");
    description = (String) capabilities.get("description");
    copyrightText = (String) capabilities.get("copyrightText");
    JSONArray layersJson = (JSONArray) capabilities.get("layers");
    // First create the layers
    for (int i=0; i<layersJson.size(); i++) {
      JSONObject layerJson = (JSONObject) layersJson.get(i);
      Long id = (Long) layerJson.get("id");
      String name = (String)layerJson.get("name");
      boolean defaultVisibility = (Boolean)layerJson.get("defaultVisibility");
      
      ArcgisRestLayer layer = new ArcgisRestLayer(this, id, name, defaultVisibility);
      layers.put(layer.getId(), layer);
    }
    // Then create the links
    for (int i=0; i<layersJson.size(); i++) {
      JSONObject layerJson = (JSONObject) layersJson.get(i);
      Long parentLayerId = (Long) layerJson.get("parentLayerId");
      if (parentLayerId >= 0) {
        Long id = (Long) layerJson.get("id");
        ArcgisRestLayer layer = layers.get(id);
        ArcgisRestLayer parentLayer = layers.get(parentLayerId);
        layer.setParentLayer(parentLayer);
        parentLayer.getSubLayers().add(layer);
      }
    }
  }
}
