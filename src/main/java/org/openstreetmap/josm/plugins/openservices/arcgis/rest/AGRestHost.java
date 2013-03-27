package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import java.io.IOException;
import java.util.List;

import org.openstreetmap.josm.plugins.openservices.Host;
import org.openstreetmap.josm.plugins.openservices.Service;
import org.openstreetmap.josm.plugins.openservices.ServiceException;
import org.openstreetmap.josm.plugins.openservices.arcgis.rest.json.HostDescriptionParser;

public class AGRestHost extends Host {
  private boolean initialized = false;
  private List<String> featureTypes;

  @Override
  public void init() throws ServiceException {
    if (initialized) return;
    initialize();
    initialized = true;
  }
  
  // TODO find neater way to setup a host like a host factory
  private void initialize() throws ServiceException {
    HttpRequest request = new HttpRequest();
    try {
      request.open("GET", getUrl());
      request.addParameter("f", "json");
      HttpResponse response = request.send();
      HostDescriptionParser.parseHostJson(response.getInputStream(), this);
      response.close();
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
    service.setFeatureName(feature);
    return service;
  }

}
