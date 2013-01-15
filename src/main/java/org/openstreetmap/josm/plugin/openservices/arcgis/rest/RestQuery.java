package org.openstreetmap.josm.plugin.openservices.arcgis.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class RestQuery {
  private String host;
  private String service;
  private Long layer;
  private ReturnType format;
  private String text = "";
  private String geometry;
  private GeometryType geometryType = GeometryType.ENVELOPE;
  private Long inSR = null;
  private Long outSR = null;
  private SpatialRel spatialRel = SpatialRel.INTERSECTS;
  private String where = "";
  private String outFields;
  private Boolean returnGeometry = true;
  
  public void setHost(String host) {
    this.host = host;
  }

  public void setService(String service) {
    this.service = service;
  }

  public void setLayer(Long layer) {
    this.layer = layer;
  }

  public void setFormat(ReturnType format) {
    this.format = format;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void setInSR(Long srid) {
    this.inSR = srid;  
  }
  
  public void setGeometry(String geometry) {
    this.geometry = geometry;
  }
  
  public void setGeometryType(GeometryType geometryType) {
    this.geometryType = geometryType;
  }

  public void setOutSR(Long outSR) {
    this.outSR = outSR;
  }

  public void setSpatialRel(SpatialRel spatialRel) {
    this.spatialRel = spatialRel;
  }

  public void setWhere(String where) {
    this.where = where;
  }

  public void setOutFields(String outFields) {
    this.outFields = outFields;
  }

  public void setReturnGeometry(Boolean returnGeometry) {
    this.returnGeometry = returnGeometry;
  }
  
  public String getHost() {
    return host;
  }

  public String getService() {
    return service;
  }

  public Long getLayer() {
    return layer;
  }

  
  public String getText() {
    return text;
  }

  public GeometryType getGeometryType() {
    return geometryType;
  }
  
  public String getGeometry() {
    return geometry;
  }

  public Long getInSR() {
    return inSR;
  }

  public Long getOutSR() {
    return outSR;
  }

  public SpatialRel getSpatialRel() {
    return spatialRel;
  }

  public String getWhere() {
    return where;
  }

  public String getOutFields() {
    return outFields;
  }

  public Boolean getReturnGeometry() {
    return returnGeometry;
  }

  private String encode(Object o) {
    if (o == null) return "";
    return o.toString();
  }
  
  private String encode(String s) {
    if (s == null) return "";
    try {
      return URLEncoder.encode(s, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return "";
    }
  }

//  public URL getUrl(ReturnType returnType) {
//    try {
//      String url = toString() + String.format("&f=%s", returnType.toString());
//      return new URL(url);
//    } catch (MalformedURLException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//      return null;
//    }
//  }
//  
//  @Override
//  public String toString() {
//    return String.format("%s/%d/query?text=%s&geometry=%s&geometryType=%s" +
//      "&inSR=%s&spatialRef=%s&where=%s&outfields=%s&returnGeometry=%s&outSR=%s",
//      host.getUrl().toString(), layer, encode(text), encode(geometry),
//      geometryType.toString(), encode(inSR), spatialRel.toString(), encode(where),
//      encode(outFields), returnGeometry.toString(), encode(outSR));
//  }
}
