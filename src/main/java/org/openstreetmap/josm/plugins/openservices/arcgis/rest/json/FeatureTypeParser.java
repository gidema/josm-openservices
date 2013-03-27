package org.openstreetmap.josm.plugins.openservices.arcgis.rest.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.util.SimpleInternationalString;
import org.opengis.feature.simple.SimpleFeatureType;
import org.openstreetmap.josm.plugins.openservices.arcgis.rest.EsriGeometryType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class FeatureTypeParser {
  private static Map<String, Class<?>> esriTypeMap = new HashMap<String, Class<?>>();
  private EsriGeometryType esriGeometryType;
  private Extent extent;
  private final SimpleFeatureTypeBuilder factory = new SimpleFeatureTypeBuilder();
  
  static {
    esriTypeMap.put("esriFieldTypeSmallInteger", Short.class);
    esriTypeMap.put("esriFieldTypeInteger", Integer.class);
    esriTypeMap.put("esriFieldTypeSingle", Double.class);
    esriTypeMap.put("esriFieldTypeDouble", Double.class);
    esriTypeMap.put("esriFieldTypeString", String.class);
    esriTypeMap.put("esriFieldTypeDate", Date.class);
    esriTypeMap.put("esriFieldTypeOID", Integer.class);
    esriTypeMap.put("esriFieldTypeGeometry", Geometry.class);
    esriTypeMap.put("esriFieldTypeBlob", null);
    esriTypeMap.put("esriFieldTypeGUID", String.class);
    esriTypeMap.put("esriFieldTypeGlobalID", String.class);
  }

  public SimpleFeatureType parse(InputStream inputStream, String namePrefix) throws JsonProcessingException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree(inputStream);
    String name = node.get("name").textValue();
    Long id = node.get("id").longValue();
    String description = node.get("description").textValue();
    esriGeometryType = EsriGeometryType.parse(
      node.get("geometryType").textValue());
    
    String featureName = name + "/" + id;
    if (namePrefix != null) {
      featureName = namePrefix + ":" + featureName;
    }
    factory.setName(featureName);
    extent = parseExtent(node.get("extent"));
    factory.setSRS(extent.getSrs());
    factory.setDescription(new SimpleInternationalString(description));
    JsonNode fields = node.get("fields");
    Iterator<JsonNode> i = fields.elements();
    while (i.hasNext()) {
      JsonNode field = i.next();
      parseAttributeDescriptor(field);
    }
    return factory.buildFeatureType();
  }
  
  private static Extent parseExtent(JsonNode node) {
    Double xmin = node.get("xmin").doubleValue();
    Double xmax = node.get("xmax").doubleValue();
    Double ymin = node.get("ymin").doubleValue();
    Double ymax = node.get("ymax").doubleValue();
    Long wkid = node.get("spatialReference").get("wkid").longValue();
    return new Extent(new Envelope(xmin, xmax, ymin, ymax), wkid);
  }

  private void parseAttributeDescriptor(JsonNode node) {
    String name = node.get("name").textValue();
    String typeName = node.get("type").textValue();
    String alias = node.get("alias").textValue();
    AttributeTypeBuilder builder = new AttributeTypeBuilder();
    Class<?> binding;
    if (typeName.equals("esriFieldTypeGeometry")) {
      binding = esriGeometryType.getBinding();
      factory.add(name, binding, extent.getSrs());
    }
    else {
      binding = esriTypeMap.get(typeName);
      factory.add(name, binding);
    }
    if (typeName.equals("esriFieldTypeOID")) {
      builder.setIdentifiable(true);
    }
    if (alias != null) {
      builder.addUserData("alias", alias);
    }
  }
  
  private static class Extent {
    private final Envelope envelope;
    private final String srs;

    public Extent(Envelope envelope, Long wkid) {
      super();
      this.envelope = envelope;
      srs = String.format("EPSG:%d", wkid);
    }
    
    public Envelope getEnvelope() {
      return envelope;
    }
    
    public String getSrs() {
      return srs;
    }
  }
}
