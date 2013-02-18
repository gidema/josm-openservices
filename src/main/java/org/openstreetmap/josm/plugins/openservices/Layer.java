package org.openstreetmap.josm.plugins.openservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.opengis.feature.Feature;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;

public class Layer {
  private String name;
  private DataSource dataSource;
  private OsmDataLayer osmLayer;
  private final Map<String, FeatureMapper> featureMappers = 
      new HashMap<String, FeatureMapper>();
  private final Map<OsmPrimitive, Feature> relatedFeatures = new HashMap<OsmPrimitive, Feature>();
 
  protected String getName() {
    return name;
  }

  protected void setName(String name) {
    this.name = name;
  }
  
  protected DataSource getDataSource() {
    return dataSource;
  }

  protected void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  private FeatureMapper getFeatureMapper(String featureName) {
    FeatureMapper mapper = featureMappers.get(featureName);
    if (mapper == null) {
      try {
        Service service = getDataSource().getService(featureName);
        mapper = OpenServices.getFeatureMapper(featureName);
        mapper.setObjectFactory(new JosmObjectFactory(getOsmLayer().data, service.getSRID()));
      } catch (ConfigurationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return mapper;
  }
  
  public void addFeatures(Service service, List<Feature> newFeatures) {
    if (newFeatures.size() == 0) return;
    String id = service.getFeatureType().getName().getLocalPart();
    FeatureMapper mapper = getFeatureMapper(id);
    for (Feature feature : newFeatures) {
      List<OsmPrimitive> primitives = mapper.mapFeature(feature);
      for (OsmPrimitive primitive : primitives) {
        relatedFeatures.put(primitive, feature);
      }
    }
  }
  
  public Feature getRelatedFeature(OsmPrimitive primitive) {
    return relatedFeatures.get(primitive);
  }
  
  private OsmDataLayer getOsmLayer() {
    if (osmLayer == null) {
      osmLayer = new OsmDataLayer(new DataSet(), name, null);
      Main.main.addLayer(osmLayer);
    }
    return osmLayer;
  }
}
