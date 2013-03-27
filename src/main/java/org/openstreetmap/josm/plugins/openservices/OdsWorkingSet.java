package org.openstreetmap.josm.plugins.openservices;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.downloadtasks.DownloadOsmTask;
import org.openstreetmap.josm.actions.downloadtasks.DownloadTask;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.DataSource;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.MapView.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.Layer;

public class OdsWorkingSet implements FeatureListener, LayerChangeListener {
  private String name;
  private final Map<String, OdsDataSource> dataSources = new HashMap<String, OdsDataSource>();
  OdsDataLayer odsDataLayer;
  OdsOsmDataLayer odsOsmDataLayer;
  //private final OdsDownloadAction downloadAction;
  private Bounds lastDownloadArea;
  private JDialog toolbox;
  private final List<OdsAction> actions = new LinkedList<OdsAction>();
  String osmQuery;
  private final Map<OsmPrimitive, Feature> relatedFeatures = new HashMap<OsmPrimitive, Feature>();
 
  public OdsWorkingSet() {
    MapView.addLayerChangeListener(this);
  }
  
  public void addAction(OdsAction action) {
    action.setWorkingSet(this);
    actions.add(action);
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
  
  public OdsDataLayer getOdsDataLayer() {
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
        toolbox.setLayout(new BoxLayout(toolbox.getContentPane(), BoxLayout.Y_AXIS));
        toolbox.setLocation(300, 300);
        toolbox.setMinimumSize(new Dimension(110,0));
        for (Action action : actions) {
          toolbox.add(new JButton(action));
        }
        toolbox.pack();
    }
    return toolbox;
  }

  public void download(Bounds area, boolean downloadOsmData) {
    lastDownloadArea = area;
    List<Future<?>> futures = new LinkedList<Future<?>>();
    List<DownloadTask> tasks = new LinkedList<DownloadTask>();
    if (downloadOsmData) {
      downloadOsmData(area, tasks, futures);
    }
    downloadOdsData(area, tasks, futures);
    Main.worker.submit(new OdsPostDownloadHandler(tasks, futures));
  }
  
  private void downloadOsmData(Bounds area, List<DownloadTask> tasks, List<Future<?>> futures) {
    Future<?> future = null;
    DownloadOsmTask task;
    activateOsmLayer();
    String query = getOsmQuery();
    if (query != null) {
      String url = getOverpassUrl(query, area);
      task = new DownloadOsmTask();
      future = task.loadUrl(false, url, null);
    }
    else {
      task = new DownloadOsmTask();
      future = task.download(false, area, null);
    }
    tasks.add(task);
    futures.add(future);
    return;
  }
  
  private void downloadOdsData(Bounds area, List<DownloadTask> tasks, List<Future<?>> futures) {
    for (OdsDataSource dataSource : getDataSources().values()) {
      OdsDownloadTask downloadTask = dataSource.getDownloadTask();
      tasks.add(downloadTask);
      Future<?> future = downloadTask.download(false, area, null);
      futures.add(future);
    }
    return;
  }
  
  private void activateOsmLayer() {
    Layer osmLayer = getOdsOsmDataLayer();
    Main.map.mapView.setActiveLayer(osmLayer);
  }
  
  public OdsOsmDataLayer getOdsOsmDataLayer() {
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
  public void featuresAdded(List<SimpleFeature> newFeatures, Bounds bounds) {
    if (newFeatures.size() == 0) return;
    String typeName = newFeatures.get(0).getFeatureType().getTypeName();
    OdsDataSource ds = dataSources.get(typeName);
    FeatureMapper mapper = ds.getFeatureMapper();
    DataSet dataSet = getOdsDataLayer().data;
    dataSet.beginUpdate();
    for (SimpleFeature feature : newFeatures) {
      List<OsmPrimitive> primitives = mapper.mapFeature(feature, dataSet);
      for (OsmPrimitive primitive : primitives) {
        relatedFeatures.put(primitive,  feature);
      }
    }
    dataSet.endUpdate();
    for (DataSource dataSource : dataSet.dataSources) {
      if (dataSource.bounds.equals(bounds)) {
        return;
      }
    }
    dataSet.dataSources.add(new DataSource(bounds, name));
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
