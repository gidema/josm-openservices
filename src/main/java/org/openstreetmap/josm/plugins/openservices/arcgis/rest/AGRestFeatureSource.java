package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import java.io.IOException;

import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.openservices.InitializationException;
import org.openstreetmap.josm.plugins.openservices.OdsDataSource;
import org.openstreetmap.josm.plugins.openservices.OdsFeatureSource;
import org.openstreetmap.josm.plugins.openservices.arcgis.rest.json.FeatureTypeParser;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaData;

public class AGRestFeatureSource implements OdsFeatureSource {
  private boolean initialized = false;
  private final AGRestHost host;
  private final String feature;
//  private String featureName;
  private final Long featureId;
  private FeatureType featureType;
  private MetaData metaData;

  protected AGRestFeatureSource(AGRestHost host, String feature) {
    super();
    this.host = host;
    this.feature = feature;
    String[] parts = feature.split("/");
    //  this.featureName = parts[0];
    this.featureId = Long.valueOf(parts[1]);
  }

  final AGRestHost getHost() {
    return host;
  }

  @Override
  public FeatureType getFeatureType() {
    return featureType;
  }
  
  @Override
  public final String getFeatureName() {
    return String.format("%s:%s", host.getName(), feature);
  }

  @Override
  public void initialize() throws InitializationException {
    if (initialized) return;
    metaData = host.getMetaData();
    HttpRequest request = new HttpRequest();
    try {
      request.open("GET", host.getUrl() + "/" + featureId);
      request.addParameter("f", "json");
      HttpResponse response = request.send();
      FeatureTypeParser parser = new FeatureTypeParser();
      featureType = parser.parse(response.getInputStream(), host.getName());
      initialized = true;
    } catch (IOException e) {
      throw new InitializationException(e);
    }
  }
  
  @Override
  public MetaData getMetaData() {
    assert initialized;
    return metaData;
  }

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
    return new AGRestDataSource(this);
  }
}
