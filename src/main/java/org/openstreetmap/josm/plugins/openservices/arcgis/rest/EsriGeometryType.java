package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public enum EsriGeometryType {
  POINT("esriGeometryPoint", Point.class),
  MULTIPOINT("esriGeometryMultipoint", MultiPoint.class),
  POLYLINE("esriGeometryPolyline", MultiLineString.class),
  POLYGON("esriGeometryPolygon", Polygon.class),
  ENVELOPE("esriGeometryEnvelope", Envelope.class);
  
  private String name;
  private Class<?> binding;

  private EsriGeometryType(String name, Class<?> binding) {
    this.name = name;
    this.binding = binding;
  }
  
  public static EsriGeometryType parse(String s) {
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
  
  public Class<?> getBinding() {
    return binding;
  }
}