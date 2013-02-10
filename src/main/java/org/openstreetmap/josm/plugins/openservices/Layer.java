package org.openstreetmap.josm.plugins.openservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;

public class Layer {
  private String name;
  private DataSource dataSource;
  private OsmDataLayer osmLayer;
  private final Map<String, FeatureMapper> featureMappers = 
      new HashMap<String, FeatureMapper>();
  private ObjectToJosmMapper featureMapper;

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

  public final void setFeatureMapper(ObjectToJosmMapper featureMapper) {
    this.featureMapper = featureMapper;
  }

  private FeatureMapper getFeatureMapper(String feature) {
    FeatureMapper mapper = featureMappers.get(feature);
    if (mapper == null) {
      try {
        mapper = OpenServices.getFeatureMapper(feature);
      } catch (ConfigurationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return mapper;
  }
  
  public void addFeatures(Service service, List<Feature> newFeatures) {
    if (newFeatures.size() == 0) return;
    int srid = service.getSRID();
    DataSet dataSet = getOsmLayer().data;
    String id = newFeatures.get(0).getName().getLocalPart();
    FeatureMapper mapper = getFeatureMapper(id);
    JosmObjectFactory objectFactory = new JosmObjectFactory(dataSet, srid);
    for (Feature feature : newFeatures) {
      mapper.mapFeature(feature, objectFactory);
    }
  }
  
  private void addFeature(SimpleFeature feature, JosmObjectFactory objectFactory) {
    featureMapper.create(feature, objectFactory);
  }

  private OsmDataLayer getOsmLayer() {
    if (osmLayer == null) {
      osmLayer = new OsmDataLayer(new DataSet(), name, null);
      Main.main.addLayer(osmLayer);
    }
    return osmLayer;
  }
}
