package org.openstreetmap.josm.plugin.openservices.arcgis.rest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.vividsolutions.jts.geom.Envelope;

public class ArcgisRestLayer {
  private Long id;
  private String name;
  private final ArcgisRestService service;
  private String type; 
  private String geometryType; 
  private String description;
  private String definitionExpression; 
  private String copyrightText; 
  private Long minScale; 
  private Long maxScale;
  private Envelope extent;
  private Long wkid;
  private List<ArcgisRestField> fields;

  private ArcgisRestLayer parentLayer;
  private boolean defaultVisibility;
  private List<ArcgisRestLayer> subLayers = new LinkedList<ArcgisRestLayer>();
  private boolean initialized = false;

  public ArcgisRestLayer(ArcgisRestService service, Long id, String name, 
      boolean defaultVisibility) {
    super();
    this.id = id;
    this.name = name;
    this.service = service;
    this.defaultVisibility = defaultVisibility;
  }

  public ArcgisRestService getService() {
    return service;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ArcgisRestLayer getParentLayer() {
    return parentLayer;
  }

  public void setParentLayer(ArcgisRestLayer parentLayer) {
    this.parentLayer = parentLayer;
  }

  public boolean isDefaultVisibility() {
    return defaultVisibility;
  }

  public void setDefaultVisibility(boolean defaultVisibility) {
    this.defaultVisibility = defaultVisibility;
  }

  public List<ArcgisRestLayer> getSubLayers() {
    return subLayers;
  }

  public void setSubLayers(List<ArcgisRestLayer> subLayers) {
    this.subLayers = subLayers;
  }
  
  public String getType() {
    return type;
  }

  public String getGeometryType() {
    return geometryType;
  }

  public String getDescription() {
    return description;
  }

  public String getDefinitionExpression() {
    return definitionExpression;
  }

  public String getCopyrightText() {
    return copyrightText;
  }

  public Long getMinScale() {
    return minScale;
  }

  public Long getMaxScale() {
    return maxScale;
  }

  public Envelope getExtent() {
    return extent;
  }

  public Long getWkid() {
    return wkid;
  }

  public List<ArcgisRestField> getFields() {
    return fields;
  }

  public String getBaseUrl() {
    return service.getBaseUrl() + "/" + id;
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
      JSONObject metaData = request.getJson();
      parseMetaData(metaData);
      initialized = true;
    } catch (MalformedURLException e) {
        throw new RuntimeException(e.getMessage());
    }
  }
  
  private void parseMetaData(JSONObject metaData) {
    type = (String) metaData.get("type");
    geometryType = (String) metaData.get("geometryType");
    description = (String) metaData.get("description");
    definitionExpression = (String) metaData.get("definitionExpression");
    copyrightText = (String) metaData.get("copyrightText");
    minScale = (Long) metaData.get("minScale");
    maxScale = (Long) metaData.get("maxScale");
    JSONObject extentJson = (JSONObject) metaData.get("extent");
    parseExtent(extentJson);
    JSONArray fieldsJson = (JSONArray) metaData.get("fields");
    // First create the layers
    fields = new ArrayList<ArcgisRestField>(fieldsJson.size());
    for (int i=0; i<fieldsJson.size(); i++) {
      JSONObject fieldJson = (JSONObject) fieldsJson.get(i);
      fields.add(parseField(fieldJson));
    }
  }
  
  private void parseExtent(JSONObject json) {
    Double xmin = ((Number) json.get("xmin")).doubleValue();
    Double xmax = ((Number) json.get("xmax")).doubleValue();
    Double ymin = ((Number) json.get("ymin")).doubleValue();
    Double ymax = ((Number) json.get("ymax")).doubleValue();
    extent = new Envelope(xmin, xmax, ymin, ymax);
    wkid = (Long) json.get("wkid");
  }

  private ArcgisRestField parseField(JSONObject json) {
    ArcgisRestField field = new ArcgisRestField();
    field.setName((String) json.get("name"));
    field.setType((String) json.get("type"));
    field.setAlias((String) json.get("alias"));
    return field;
  }
}
