package org.openstreetmap.josm.plugin.openservices.arcgis.rest;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

public abstract class ArcgisJsonParser<T> {
  private final GeometryFactory geoFactory;
  
  public ArcgisJsonParser(int srid) {
    PrecisionModel precisionModel = new PrecisionModel();
    this.geoFactory = new GeometryFactory(precisionModel, srid);
  }
  
  public abstract T parse(ArcgisRestLayer layer, JSONObject jsonObject);
  
  protected Geometry parseGeometry(String geometryType, JSONObject jsonObject) {
    if (geometryType.equals("esriGeometryPolyline")) {
      return parsePolyLine(jsonObject);
    }
    if (geometryType.equals("esriGeometryPolygon")) {
      return parsePolygon(jsonObject);
    }
    if (geometryType.equals("esriGeometryPoint")) {
      return parsePoint(jsonObject);
    }
    return null;
  }

  protected MultiLineString parsePolyLine(JSONObject geometry) {
    JSONArray paths = (JSONArray) geometry.get("paths");
    LineString[] lineStrings = new LineString[paths.size()];
    for (int i=0; i<paths.size(); i++) {
      lineStrings[i] = parsePath((JSONArray) paths.get(i));
    }
    return geoFactory.createMultiLineString(lineStrings);
  }

  protected Polygon parsePolygon(JSONObject geometry) {
    JSONArray rings = (JSONArray) geometry.get("rings");
    LinearRing shell = parseRing((JSONArray)rings.get(0));
    LinearRing[] holes = new LinearRing[rings.size() - 1];
    for (int i=1; i<rings.size(); i++) {
      holes[i - 1] = parseRing((JSONArray)rings.get(i));
    }
    return geoFactory.createPolygon(shell, holes);
  }
  
  protected LineString parsePath(JSONArray jsonPath) {
    @SuppressWarnings({ "cast", "unchecked" })
    ArrayList<JSONArray> jsonLine = (ArrayList<JSONArray>)jsonPath;
    Coordinate[] coords = new Coordinate[jsonPath.size()];
    int i=0;
    for (JSONArray jsonPoint : jsonLine) {
      coords[i++] = parseCoordinate(jsonPoint);
    }
    return geoFactory.createLineString(coords);
  }
  
  protected LinearRing parseRing(JSONArray jsonArray) {
    @SuppressWarnings({ "cast", "unchecked" })
    ArrayList<JSONArray> jsonRing = (ArrayList<JSONArray>)jsonArray;
    Coordinate[] coords = new Coordinate[jsonRing.size()];
    int i=0;
    for (JSONArray jsonPoint : jsonRing) {
      coords[i++] = parseCoordinate(jsonPoint);
    }
    return geoFactory.createLinearRing(coords);
  }
  
  protected Point parsePoint(JSONObject jsonPoint) {
    return null;
  }

  protected Point parsePoint(JSONArray jsonPoint) {
    Coordinate coordinate = parseCoordinate(jsonPoint);
    return geoFactory.createPoint(coordinate);
  }
  
  private static Coordinate parseCoordinate(JSONArray jsonPoint) {
    Coordinate coordinate = new Coordinate();
    coordinate.x = ((Number) jsonPoint.get(0)).doubleValue();
    coordinate.y = ((Number) jsonPoint.get(1)).doubleValue();
    return coordinate;
  }

}
