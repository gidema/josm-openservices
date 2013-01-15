package org.openstreetmap.josm.plugin.openservices.arcgis.rest;

public enum GeometryType {
  POINT("esriGeometryPoint"),
  MULTIPOINT("esriGeometryMultipoint"),
  POLYLINE("esriGeometryPolyline"),
  POLYGON("esriGeometryPolygon"),
  ENVELOPE("esriGeometryEnvelope");
  
  private String name;

  private GeometryType(String name) {
    this.name = name;
  }
  
  public static GeometryType parse(String s) {
    if (s.equals("esriGeometryPoint")) return POINT;
    if (s.equals("esriGeometryMultipoint")) return MULTIPOINT;
    if (s.equals("esriGeometryPolyline")) return POLYLINE;
    if (s.equals("esriGeometryPolygon")) return POLYGON;
    if (s.equals("esriGeometryEnvelope")) return ENVELOPE;
    throw new RuntimeException("Unknown geometry type: " + s);
  }
  @Override
  public String toString() {
    return name;
  }
}