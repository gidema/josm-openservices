package org.openstreetmap.josm.plugins.openservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.MapView.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;

public class OdsLayer implements FeatureListener {
  private String name;
  private final Map<String, OdsDataSource> dataSources = new HashMap<String, OdsDataSource>();
  OsmDataLayer osmLayer;
  String osmQuery;
  private final Map<OsmPrimitive, Feature> relatedFeatures = new HashMap<OsmPrimitive, Feature>();
 
  protected String getName() {
    return name;
  }

  protected void setName(String name) {
    this.name = name;
  }
  
  public final Map<String, OdsDataSource> getDataSources() {
    return dataSources;
  }

  public void setOsmQuery(String query) {
    /**
     * Currently, we pass the osm (overpass) query through http get.
     * This doesn't allow linefeed or carriage return characters,
     * so we need to strip them.
     */
    if (query == null) {
      osmQuery = null;
      return;
    }
    this.osmQuery = query.replaceAll("[\n\r\t]", "");
  }
  
  


  public final String getOsmQuery() {
    return osmQuery;
  }

  public Feature getRelatedFeature(OsmPrimitive primitive) {
    return relatedFeatures.get(primitive);
  }
  
  OsmDataLayer getOsmLayer() {
    if (osmLayer == null) {
      osmLayer = new ServiceDataLayer(name);
      MapView.addLayerChangeListener(new LayerChangeListener() {
        @Override
        public void activeLayerChange(Layer oldLayer, Layer newLayer) {
          // TODO Auto-generated method stub
        }

        @Override
        public void layerAdded(Layer newLayer) {
          // TODO Auto-generated method stub
        }

        @Override
        public void layerRemoved(Layer oldLayer) {
          if (oldLayer == osmLayer) {
            osmLayer = null;
          }
        }
      });
      Main.main.addLayer(osmLayer);
    }
    return osmLayer;
  }

  @Override
  public void featureAdded(Feature feature) {
    String typeName = feature.getName().toString();
    OdsDataSource ds = dataSources.get(typeName);
    FeatureMapper mapper = ds.getFeatureMapper();
    List<OsmPrimitive> primitives = mapper.mapFeature(feature, getOsmLayer().data);
    for (OsmPrimitive primitive : primitives) {
      relatedFeatures.put(primitive,  feature);
    }
  }

  @Override
  public void featuresAdded(List<SimpleFeature> newFeatures) {
    if (newFeatures.size() == 0) return;
    String typeName = newFeatures.get(0).getFeatureType().getTypeName();
    OdsDataSource ds = dataSources.get(typeName);
    FeatureMapper mapper = ds.getFeatureMapper();
    for (SimpleFeature feature : newFeatures) {
      List<OsmPrimitive> primitives = mapper.mapFeature(feature, getOsmLayer().data);
      for (OsmPrimitive primitive : primitives) {
        relatedFeatures.put(primitive,  feature);
      }
    }
  }

  public void addDataSource(OdsDataSource dataSource) {
    dataSources.put(dataSource.getFeatureType(), dataSource);
    dataSource.addFeatureListener(this);
  }
  
  
}
