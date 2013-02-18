package org.openstreetmap.josm.plugins.openservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.openstreetmap.josm.data.osm.OsmPrimitive;

import com.vividsolutions.jts.geom.Geometry;

public class DefaultFeatureMapper implements FeatureMapper {
  private final Map<String, String> fixedTags = new HashMap<String, String>();
  private final Map<String, PropertyMapper> propertyTags = new HashMap<String, PropertyMapper>();
  private GeometryMapper geometryMapper;
  private String featureName;
  private String primitiveType;
  
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
  
  public void setGeometryMapper(GeometryMapper geometryMapper) {
    this.geometryMapper = geometryMapper;    
  }

  @Override
  public void setObjectFactory(JosmObjectFactory objectFactory) {
    // TODO Use factory for FeatureMappers and GeometryMappers to achieve this
    geometryMapper.setObjectFactory(objectFactory);
  }

  @Override
  public List<OsmPrimitive> mapFeature(Feature feature) {
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
    return geometryMapper.createPrimitives(geometry, tags);
  }
}