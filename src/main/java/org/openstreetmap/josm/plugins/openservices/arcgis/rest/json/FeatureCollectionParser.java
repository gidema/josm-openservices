package org.openstreetmap.josm.plugins.openservices.arcgis.rest.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.util.Converters;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.openstreetmap.josm.plugins.openservices.crs.CRSUtil;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

public class FeatureCollectionParser {
  private final GeometryFactory geoFactory;
  private final SimpleFeatureType featureType;
  private final SimpleFeatureBuilder featureBuilder;
  private final JsonFactory factory = new JsonFactory();
  private final ObjectMapper mapper = new ObjectMapper();
  private final Class<?> geometryClass;
  private String id = null;
  
  public FeatureCollectionParser(SimpleFeatureType featureType) {
    this.featureType = featureType;
    this.featureBuilder = new SimpleFeatureBuilder(featureType);
    GeometryType geometryType = featureType.getGeometryDescriptor().getType();
    this.geometryClass = geometryType.getBinding();
    int srid = CRSUtil.getSrid(geometryType.getCoordinateReferenceSystem());
    PrecisionModel precisionModel = new PrecisionModel();
    this.geoFactory = new GeometryFactory(precisionModel, srid);
  }
  
  public SimpleFeatureCollection parse(InputStream is) throws JsonParseException, IOException {
    JsonParser jp = factory.createJsonParser(is);
    jp.nextToken();
    jp.nextToken();
    if ("error".equals(jp.getCurrentName())) {
      JsonNode node = mapper.readTree(jp);
      parseError(node);
    }
    // Skip until the features attribute
    while (!jp.isClosed() && !"features".equals(jp.getCurrentName())) {
      jp.nextToken();
    }
    if (jp.isClosed()) {
      throw new IOException("No features found in json data");
    }
    JsonToken token = jp.nextToken(); // START_ARRAY
    if (token != JsonToken.START_ARRAY) {
      throw new JsonParseException("Unexpected token: " + token.toString(), jp.getCurrentLocation());
    }
    List<SimpleFeature> featureList = new LinkedList<SimpleFeature>();
    token = jp.nextToken(); // START_OBJECT or END_ARRAY
//    SimpleFeatureType featureType = (SimpleFeatureType)odsFeatureSource.getFeatureType();
    while (token != JsonToken.END_ARRAY) {
      SimpleFeature feature = parseFeature(jp);
      featureList.add(feature);
      token = jp.nextToken(); // START_OBJECT or END_ARRAY
    }
    jp.close();
    return DataUtilities.collection(featureList);
  }
  
  private SimpleFeature parseFeature(JsonParser jp) throws JsonParseException, IOException {
    JsonNode node = mapper.readTree(jp);
    featureBuilder.reset();
    Name geometryAttribute = featureType.getGeometryDescriptor().getName();
    featureBuilder.set(geometryAttribute, parseGeometry(node.get("geometry")));
    Iterator<Entry<String, JsonNode>> i = node.get("attributes").fields();
    while (i.hasNext()) {
      Entry<String, JsonNode> entry = i.next();
      String name = entry.getKey();
      // TODO move this out of the loop
      AttributeType attributeType = featureType.getType(name);
      if (attributeType != null) {
        Class<?> binding = featureType.getType(name).getBinding();
        Object value = getValue(entry.getValue(), binding);
        featureBuilder.set(name, value);
        if (binding == Serializable.class) {
          id = value.toString();
        }
      }
    }
    SimpleFeature feature = featureBuilder.buildFeature(id);
    return feature;
  }
  
  private Object getValue(JsonNode node, Class<?> binding) {
    if (node.isNull()) {
      return null;
    }
    if (node.isTextual()) {
      return Converters.convert(node.textValue(), binding);
    }
    if (node.isNumber()) {
      return Converters.convert(node.numberValue(), binding);
    }
    if (node.isBinary()) {
      return Converters.convert(node.booleanValue(), binding);
    }
    return null;
  }
  
  protected Geometry parseGeometry(JsonNode node) {
    if (geometryClass == MultiLineString.class) {
      return parsePolyLine(node);
    }
    if (geometryClass == Polygon.class) {
      return parsePolygon(node);
    }
    if (geometryClass == Point.class) {
      return parsePoint(node);
    }
    return null;
  }

  protected MultiLineString parsePolyLine(JsonNode node) {
    JsonNode paths = node.get("paths");
    LineString[] lineStrings = new LineString[paths.size()];
    for (int i=0; i<paths.size(); i++) {
      lineStrings[i] = parsePath(paths.get(i));
    }
    return geoFactory.createMultiLineString(lineStrings);
  }

  protected Polygon parsePolygon(JsonNode node) {
    JsonNode rings = node.get("rings");
    LinearRing shell = parseRing(rings.get(0));
    LinearRing[] holes = new LinearRing[rings.size() - 1];
    for (int i=1; i<rings.size(); i++) {
      holes[i - 1] = parseRing(rings.get(i));
    }
    return geoFactory.createPolygon(shell, holes);
  }
  
  protected LineString parsePath(JsonNode node) {
    @SuppressWarnings({ "cast", "unchecked" })
    Coordinate[] coords = new Coordinate[node.size()];
    for (int i=0; i<node.size(); i++) {
      coords[i] = parseCoordinate(node.get(i));
    }
    return geoFactory.createLineString(coords);
  }
  
  protected LinearRing parseRing(JsonNode node) {
    Coordinate[] coords = new Coordinate[node.size()];
    Iterator<JsonNode> it = node.elements();
    int i=0;
    while (it.hasNext()) {
      coords[i++] = parseCoordinate(it.next());
    }
    return geoFactory.createLinearRing(coords);
  }
  
  protected Point parsePoint(JsonNode node) {
    Coordinate coordinate = parseCoordinate(node);
    return geoFactory.createPoint(coordinate);
  }
  
  private void parseError(JsonNode node) throws IOException {
    JsonNode errorNode = node.get("error");
    JsonNode details = errorNode.get("details");
    throw new IOException(details.toString());
  }
  
  private static Coordinate parseCoordinate(JsonNode node) {
    Coordinate coordinate = new Coordinate();
    coordinate.x = node.get(0).doubleValue();
    coordinate.y = node.get(1).doubleValue();
    return coordinate;
  }
}
