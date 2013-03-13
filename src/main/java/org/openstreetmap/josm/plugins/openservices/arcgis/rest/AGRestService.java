package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.openservices.Host;
import org.openstreetmap.josm.plugins.openservices.OdsDataSource;
import org.openstreetmap.josm.plugins.openservices.Service;
import org.openstreetmap.josm.plugins.openservices.ServiceException;

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
    JSONHttpRequest request = new JSONHttpRequest();
    try {
      request.open("GET", host.getUrl() + "/" + featureId);
      request.addParameter("f", "json");
      request.send();
      JSONObject json = request.getJson();
      AGRestFeatureTypeFactory featureTypeFactory = new AGRestFeatureTypeFactory();
      featureType = featureTypeFactory.createFeatureType(json, host);
    } catch (IOException e) {
      throw new ServiceException(e);
    } catch (FactoryException e) {
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
    
//  private RestQuery getQuery(Bounds bounds) {
//    RestQuery query = new RestQuery();  
//    query.setService(this);
//    query.setInSR(4326L);
//    query.setGeometry(formatBounds(bounds));
//    query.setOutFields("*");
//    return query;
//  }
//  
//  private static String formatBounds(Bounds bounds) {
//    return String.format("%f,%f,%f,%f",
//        bounds.getMin().getX(), bounds.getMin().getY(),
//        bounds.getMax().getX(), bounds.getMax().getY());
//  }


//  class AGRestDownloadTask implements Callable<FeatureCollection<?, ?>> {
//    private FeatureCollection<?, ?> features;
//    private final RestQuery query;
//    
//    public AGRestDownloadTask(RestQuery query) {
//      this.query = query;
//    }
//
//    @Override
//    public FeatureCollection<?, ?> call() throws Exception {
//      init();
//      AGRestReader reader = new AGRestReader(query);
//      JSONObject json = reader.getJson();
//      AGRestFeatureParser featureParser = new AGRestFeatureParser(AGRestService.this);
//      return featureParser.parse(json);
//    }
//  }


  @Override
  public OdsDataSource newDataSource() {
    return new AGRestDataSource();
  }
}
