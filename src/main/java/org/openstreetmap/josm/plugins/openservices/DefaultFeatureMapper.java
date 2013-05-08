package org.openstreetmap.josm.plugins.openservices;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.opengis.feature.Feature;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaData;

import com.vividsolutions.jts.geom.Geometry;

public class DefaultFeatureMapper implements FeatureMapper {
  private MetaData context;
  private final List<TagBuilder> tagBuilders = new LinkedList<TagBuilder>();
  private GeometryMapper geometryMapper;
  private String featureName;
  
  
  @Override
  public void setContext(MetaData context) {
    this.context = context;
  }

  @Override
  public final String getFeatureName() {
    return featureName;
  }

  public final void setFeatureName(String featureName) {
    this.featureName = featureName;
  }

  public void addTagBuilder(TagBuilder tagBuilder) {
    tagBuilders.add(tagBuilder);
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
  public List<OsmPrimitive> mapFeature(Feature feature, DataSet dataSet) {
    Map<String, String> tags = new HashMap<String, String>();
    for (TagBuilder tagBuilder : tagBuilders) {
      tagBuilder.createTag(tags, feature);
    }
    Geometry geometry = (Geometry) feature.getDefaultGeometryProperty().getValue();
    return geometryMapper.createPrimitives(geometry, tags, dataSet);
  }
}