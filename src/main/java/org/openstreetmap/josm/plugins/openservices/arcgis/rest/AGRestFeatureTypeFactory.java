package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.geotools.util.SimpleInternationalString;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.openservices.Host;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;

public class AGRestFeatureTypeFactory {
  //private static NameFactory nameFactory = BasicFactories.getDefault().getNameFactory();
  private static Map<Object, Class<?>> esriTypeMap = new HashMap<Object, Class<?>>();
  private String esriGeometryType;
  private CoordinateReferenceSystem crs;
  
  static {
    esriTypeMap.put("esriFieldTypeString", String.class);
    esriTypeMap.put("esriFieldTypeDouble", Double.class);
    esriTypeMap.put("esriFieldTypeDate", Date.class);
    esriTypeMap.put("esriFieldTypeOID", Serializable.class);
    esriTypeMap.put("esriFieldTypeGeometry", Geometry.class);
    esriTypeMap.put("esriGeometryPolyline", MultiLineString.class);
  }

  public SimpleFeatureType createFeatureType(JSONObject json, Host host) throws FactoryException {
    SimpleFeatureTypeBuilder factory = new SimpleFeatureTypeBuilder();
    factory.setName(String.format("%s:%s/%d", host.getName(), json.get("name"), json.get("id")));
    Extent extent = parseExtent((JSONObject) json.get("extent"));
    crs = CRS.decode(extent.srs);
    factory.setCRS(crs);
    factory.setDescription(new SimpleInternationalString((String) json.get("description")));
    esriGeometryType = (String) json.get("geometryType");
    //GeometryType geometryType = createGeometryType(esriGeometryType, extent.srs);
    //String type = (String) json.get("type");
    //String definitionExpression = (String) metaData.get("definitionExpression");
    //String copyrightText = (String) metaData.get("copyrightText");
    //Long minScale = (Long) metaData.get("minScale");
    //Long maxScale = (Long) metaData.get("maxScale");
    JSONArray fieldsJson = (JSONArray) json.get("fields");
    //List<PropertyDescriptor> schema = new ArrayList<PropertyDescriptor>(fieldsJson.size());
    for (int i=0; i<fieldsJson.size(); i++) {
      JSONObject fieldJson = (JSONObject) fieldsJson.get(i);
      AttributeDescriptor descriptor = createAttributeDescriptor(fieldJson);
      factory.add(descriptor);
      if (Geometry.class.isAssignableFrom(descriptor.getType().getBinding())) {
        factory.setDefaultGeometry(descriptor.getLocalName());
      }
    }
    return factory.buildFeatureType();
  }
  
  private static Extent parseExtent(JSONObject json) throws FactoryException {
    Double xmin = ((Number) json.get("xmin")).doubleValue();
    Double xmax = ((Number) json.get("xmax")).doubleValue();
    Double ymin = ((Number) json.get("ymin")).doubleValue();
    Double ymax = ((Number) json.get("ymax")).doubleValue();
    Long wkid = (Long) ((JSONObject)json.get("spatialReference")).get("wkid");
    return new Extent(new Envelope(xmin, xmax, ymin, ymax), wkid);
  }

  private AttributeDescriptor createAttributeDescriptor(JSONObject json) {
    String name = (String) json.get("name");
    String typeName = (String) json.get("type");
    String alias = (String) json.get("alias");
    AttributeTypeBuilder builder = new AttributeTypeBuilder();
    Class<?> binding;
    if (typeName.equals("esriFieldTypeGeometry")) {
      binding = esriTypeMap.get(esriGeometryType);
      builder.setCRS(crs);
    }
    else {
      binding = esriTypeMap.get(typeName);
    }
    builder.setName(name);
    builder.setBinding(binding);
    builder.setIdentifiable(name.equals("SHAPE.FID"));
    if (alias != null) {
      builder.addUserData("alias", alias);
    }
    return builder.buildDescriptor(name);
  }
  
//  private static GeometryType createGeometryType(String esriGeometryType,
//      CoordinateReferenceSystem crs) {
//    Name name = new NameImpl("geometry");
//    Class<?> binding = esriTypeMap.get(esriGeometryType);
//    return factory.createGeometryType(name, binding, crs, false, false,
//        Collections.emptyList(), null, null);
//  }

  private static class Extent {
    Envelope envelope;
    String srs;

    public Extent(Envelope envelope, Long wkid) throws FactoryException {
      super();
      this.envelope = envelope;
      srs = String.format("EPSG:%d", wkid);
    }
  }
}
