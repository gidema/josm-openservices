package org.openstreetmap.josm.plugins.openservices;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JDialog;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.downloadtasks.DownloadOsmTask;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.MapView.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.Layer;

public class OdsWorkingSet implements FeatureListener, LayerChangeListener {
  private String name;
  private final Map<String, OdsDataSource> dataSources = new HashMap<String, OdsDataSource>();
  OdsDataLayer odsDataLayer;
  OdsOsmDataLayer odsOsmDataLayer;
  private final OdsDownloadAction downloadAction;
  private JDialog toolbox;
  String osmQuery;
  private final Map<OsmPrimitive, Feature> relatedFeatures = new HashMap<OsmPrimitive, Feature>();
 
  public OdsWorkingSet() {
    this.downloadAction = new OdsDownloadAction(this);
    MapView.addLayerChangeListener(this);
  }
  
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
    this.osmQuery = query.replaceAll("\\s", "");
  }
  
  public final String getOsmQuery() {
    return osmQuery;
  }

  public Feature getRelatedFeature(OsmPrimitive primitive) {
    return relatedFeatures.get(primitive);
  }
  
  private OdsDataLayer getOdsDataLayer() {
    if (odsDataLayer == null) {
      odsDataLayer = new OdsDataLayer("ODS: " + name);
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
          if (oldLayer == odsDataLayer) {
            odsDataLayer = null;
          }
        }
      });
      Main.main.addLayer(odsDataLayer);
    }
    return odsDataLayer;
  }

  public void addDataSource(OdsDataSource dataSource) {
    dataSources.put(dataSource.getFeatureType(), dataSource);
    dataSource.addFeatureListener(this);
  }
  
  public JDialog getToolbox() {
    if (toolbox == null) {
        toolbox = new JDialog((Frame) Main.main.parent, "ODS");
        toolbox.add(new JButton(downloadAction));
        toolbox.setLocation(300, 300);
        toolbox.setMinimumSize(new Dimension(110,0));
        toolbox.pack();
    }
    return toolbox;
  }

  public void download(Bounds area, boolean downloadOsmData) {
    Layer activeLayer = null;
    if (Main.isDisplayingMapView()) {
      activeLayer = Main.map.mapView.getActiveLayer();
    }
    if (downloadOsmData) {
      downloadOsmData(area);
    }
    downloadOdsData(area);
  }
  
  private void downloadOsmData(Bounds area) {
    Future<?> future1 = null;
    DownloadOsmTask task;
    activateOsmLayer();
    String query = getOsmQuery();
    if (query != null) {
      String url = getOverpassUrl(query, area);
      task = new DownloadOsmTask();
      future1 = task.loadUrl(false, url, null);
    }
    else {
      task = new DownloadOsmTask();
      future1 = task.download(false, area, null);
    }
    Main.worker.submit(new org.openstreetmap.josm.actions.downloadtasks.PostDownloadHandler(task, future1));
  }
  
  private void downloadOdsData(Bounds area) {
    for (OdsDataSource dataSource : getDataSources().values()) {
      OdsDownloadTask downloadTask = dataSource.getDownloadTask();
      Future<?> future2 = downloadTask.download(false, area, null);
      Main.worker.submit(new PostDownloadHandler(future2));
    }
  }
  
  private void activateOsmLayer() {
    Layer osmLayer = getOdsOsmDataLayer();
    Main.map.mapView.setActiveLayer(osmLayer);
  }
  
  private  OdsOsmDataLayer getOdsOsmDataLayer() {
    if (odsOsmDataLayer == null) {
      odsOsmDataLayer = new OdsOsmDataLayer("OSM: " + name);
      Main.main.addLayer(odsOsmDataLayer);
    }
    return odsOsmDataLayer;
  }
  
  private static String getOverpassUrl(String query, Bounds bounds) {
    String host = "http://overpass-api.de/api";
    String bbox = String.format(Locale.ENGLISH, "%f,%f,%f,%f", bounds.getMin().getY(), bounds.getMin().getX(), 
        bounds.getMax().getY(), bounds.getMax().getX());
    String q = query.replaceAll("\\$bbox", bbox);
    q = q.replaceAll("\\{\\{bbox\\}\\}", bbox);
    q = q.replace(";$", "");
    return String.format("%s/interpreter?data=%s;out meta;", host, q);
  }

  // Implement FeatureListener
  @Override
  public void featureAdded(Feature feature) {
    String typeName = feature.getName().toString();
    OdsDataSource ds = dataSources.get(typeName);
    FeatureMapper mapper = ds.getFeatureMapper();
    List<OsmPrimitive> primitives = mapper.mapFeature(feature, getOdsDataLayer().data);
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
      List<OsmPrimitive> primitives = mapper.mapFeature(feature, getOdsDataLayer().data);
      for (OsmPrimitive primitive : primitives) {
        relatedFeatures.put(primitive,  feature);
      }
    }
  }

  // Implement LayerChangeListener

  @Override
  public void activeLayerChange(Layer oldLayer, Layer newLayer) {
    if (newLayer == odsDataLayer || newLayer == odsOsmDataLayer) {
      getToolbox().setVisible(true);
    }
    else {
      getToolbox().setVisible(false);
    }
  }

  @Override
  public void layerAdded(Layer newLayer) {
    // No action required
  }

  @Override
  public void layerRemoved(Layer oldLayer) {
    if (oldLayer == odsDataLayer) {
      toolbox.setVisible(false);
    }
  }
}
