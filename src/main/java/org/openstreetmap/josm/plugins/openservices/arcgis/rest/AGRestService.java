package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import java.io.IOException;

import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.openservices.Host;
import org.openstreetmap.josm.plugins.openservices.OdsDataSource;
import org.openstreetmap.josm.plugins.openservices.Service;
import org.openstreetmap.josm.plugins.openservices.ServiceException;
import org.openstreetmap.josm.plugins.openservices.arcgis.rest.json.FeatureTypeParser;

public class AGRestService implements Service {
  private boolean initialized = false;
  private Host host;
  private String feature;
//  private String featureName;
  private Long featureId;
  private FeatureType featureType;

  @Override
  public void setHost(Host host) {
    this.host = host;
  }

  final AGRestHost getHost() {
    return (AGRestHost) host;
  }

  @Override
  public FeatureType getFeatureType() {
    return featureType;
  }
  
  @Override
  public void setFeatureName(String feature) throws ServiceException {
    this.feature = feature;
    String[] parts = feature.split("/");
//    this.featureName = parts[0];
    this.featureId = Long.valueOf(parts[1]);
  }

  @Override
  public final String getFeatureName() {
    return String.format("%s:%s", host.getName(), feature);
  }

  @Override
  public void init() throws ServiceException {
    if (initialized) return;
    initialize();
    initialized = true;
  }
  
  private void initialize() throws ServiceException {
    HttpRequest request = new HttpRequest();
    try {
      request.open("GET", host.getUrl() + "/" + featureId);
      request.addParameter("f", "json");
      HttpResponse response = request.send();
      FeatureTypeParser parser = new FeatureTypeParser();
      featureType = parser.parse(response.getInputStream(), host.getName());
    } catch (IOException e) {
      throw new ServiceException(e);
    }
  }
  
//  public FutureTask<FeatureCollection<?, ?>> getDownloadTask(Bounds bounds) {
//    RestQuery query = getQuery(bounds);
//    return new FutureTask<FeatureCollection<?, ?>>(new AGRestDownloadTask(query));
//  }

  public Long getFeatureId(){
    return featureId;
  }
  
  @Override
  public CoordinateReferenceSystem getCrs() {
    return featureType.getGeometryDescriptor().getCoordinateReferenceSystem();
  }

  @Override
  public String getSRS() {
    ReferenceIdentifier rid = getCrs().getIdentifiers().iterator().next();
    return rid.toString();
  }
  
  @Override
  public Long getSRID() {
    ReferenceIdentifier rid = getCrs().getIdentifiers().iterator().next();
    return Long.parseLong(rid.getCode());
  }
    
  @Override
  public OdsDataSource newDataSource() {
    return new AGRestDataSource();
  }
}
