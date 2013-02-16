package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;
import org.openstreetmap.josm.plugins.openservices.Host;
import org.openstreetmap.josm.plugins.openservices.Service;
import org.openstreetmap.josm.plugins.openservices.ServiceException;
import org.openstreetmap.josm.plugins.openservices.arcgis.rest.json.JsonParser;

public class AGRestHost extends Host {
  private boolean initialized = false;
  private List<String> featureTypes;

  @Override
  public void init() throws ServiceException {
    if (initialized) return;
    initialize();
    initialized = true;
  }
  
  private void initialize() throws ServiceException {
    JSONHttpRequest request = new JSONHttpRequest();
    try {
      request.open("GET", getUrl());
      request.addParameter("f", "json");
      request.send();
      JSONObject json = request.getJson();
      JsonParser.parseHostJson(this, json);
    } catch (IOException e) {
      throw new ServiceException(e);
    }
  }
  
  public void setFeatureTypes(List<String> featureTypes) {
    this.featureTypes = featureTypes;
  }

  @Override
  public boolean hasFeatureType(String feature) throws ServiceException {
    init();
    return featureTypes.contains(feature);
  }

  @Override
  public Service getService(String feature) throws ServiceException {
    AGRestService service = new AGRestService();
    service.setHost(this);
    service.setFeature(feature);
    return service;
  }

}
