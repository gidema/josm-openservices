package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.geotools.data.DataUtilities;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.openstreetmap.josm.plugins.openservices.Service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

public class AGRestFeatureParser {
  private final GeometryFactory geoFactory;
  private final Service service;
  private final GeometryDescriptor geometryDescriptor;
  
  public AGRestFeatureParser(Service service) {
    this.service = service;
    this.geometryDescriptor = service.getFeatureType().getGeometryDescriptor();
    PrecisionModel precisionModel = new PrecisionModel();
    this.geoFactory = new GeometryFactory(precisionModel, service.getSRID().intValue());
  }
  
  public FeatureCollection parse(JSONObject json) {
    JSONArray features = (JSONArray) json.get("features");
    List<SimpleFeature> featureList = new ArrayList<SimpleFeature>(features.size());
    for (int i=1; i < features.size(); i++) {
      SimpleFeature feature = parseFeature((JSONObject) features.get(i));
      featureList.add(feature);
    }
    return DataUtilities.collection(featureList);
  }
  
  private SimpleFeature parseFeature(JSONObject json) {
    JSONObject geometryJson = (JSONObject) json.get("geometry");
    JSONObject attributes = (JSONObject) json.get("attributes");
    Class<?> geometryClass = geometryDescriptor.getType().getBinding();
    Geometry geometry = parseGeometry(geometryClass, geometryJson);
    Collection<PropertyDescriptor> propertyDescriptors = service.getFeatureType().getDescriptors();
    Object[] values = new Object[propertyDescriptors.size()];
    SimpleFeatureType featureType = (SimpleFeatureType) service.getFeatureType();
    String id = null;
    int i=0;
    for (PropertyDescriptor descriptor : propertyDescriptors) {
      Name name = descriptor.getName();
      Object o = attributes.get(name.getLocalPart());
      values[i++] = o;
      if (descriptor.getType().getBinding() ==Serializable.class) {
        id = o.toString();
      }
    }
    SimpleFeature feature = SimpleFeatureBuilder.build(featureType, values, id);

    feature.setDefaultGeometry(geometry);
    return feature;
  }

  protected Geometry parseGeometry(Class<?> geometryClass, JSONObject jsonObject) {
    if (geometryClass == MultiLineString.class) {
      return parsePolyLine(jsonObject);
    }
    if (geometryClass == Polygon.class) {
      return parsePolygon(jsonObject);
    }
    if (geometryClass == Point.class) {
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
