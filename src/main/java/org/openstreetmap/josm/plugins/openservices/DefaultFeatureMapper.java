package org.openstreetmap.josm.plugins.openservices;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.openstreetmap.josm.data.osm.OsmPrimitive;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

public class DefaultFeatureMapper implements FeatureMapper {
  private final Map<String, String> fixedTags = new HashMap<String, String>();
  private final Map<String, PropertyMapper> propertyTags = new HashMap<String, PropertyMapper>();
  private String featureName;
  private String primitiveType;
  private JosmObjectFactory objectFactory;
  
  @Override
  public final String getFeatureName() {
    return featureName;
  }

  public final void setFeatureName(String featureName) {
    this.featureName = featureName;
  }

  public void addFixedTag(String key, String value) {
    fixedTags.put(key, value);
  }
  
  public void addPropertyMapper(String key, String property, String format) {
    PropertyMapper propertyMapper = new PropertyMapper();
    propertyMapper.setKey(key);
    propertyMapper.setProperty(property);
    propertyMapper.setFormat(format);
    propertyTags.put(property, propertyMapper);
  }
  
  public void setPrimitiveType(String primitiveType) {
    this.primitiveType = primitiveType;
  }
  
  @Override
  public void mapFeature(Feature feature, JosmObjectFactory factory) {
    // TODO fix this
    this.objectFactory = factory;
    Map<String, String> tags = new HashMap<String, String>();
    tags.putAll(fixedTags);
    for (Entry<String, PropertyMapper> entry : propertyTags.entrySet()) {
      Property property = feature.getProperty(entry.getKey());
      if (property != null && property.getValue() != null) {
        PropertyMapper pMapper = entry.getValue();
        tags.put(pMapper.getKey(), pMapper.map(property.getValue()));
      }
    }
    Geometry geometry = (Geometry) feature.getDefaultGeometryProperty().getValue();
    createPrimitive(geometry, tags);
  }
  
  protected void createPrimitive(Geometry geometry, Map<String, String> tags) {
    if (geometry instanceof GeometryCollection) {
      GeometryCollection gc = (GeometryCollection)geometry;
      for (int i = 0; i < gc.getNumGeometries(); i++) {
        createPrimitive(gc.getGeometryN(i), tags);
      }
      return;
    }
    OsmPrimitive primitive = null;
    if (primitiveType.equals("way")) {
      if (geometry instanceof LineString) {
        primitive = objectFactory.buildWay((LineString)geometry);
      }
      else if (geometry instanceof Polygon) {
        Polygon polygon = (Polygon) geometry;
        if (polygon.getNumInteriorRing() == 0) {
          primitive = objectFactory.buildWay(polygon);
        }
        else {
          primitive = objectFactory.buildMultiPolygon(polygon);
        }
      }
    } else if (primitiveType.equals("polygon")) {
      primitive = objectFactory.buildPolygon((Polygon)geometry);
    }
    for (Entry<String, String> entry : tags.entrySet()) {
      primitive.put(entry.getKey(), entry.getValue());
    }
  }
}