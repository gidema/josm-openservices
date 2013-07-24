package org.openstreetmap.josm.plugins.openservices;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.DataSource;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.MapView.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.openservices.tags.FeatureMapper;

/**
 * The OdsWorkingSet is the main component of the ODS plugin.
 * It manages a pair of interrelated layers which are a normal OSM layer
 * and a ODS layer.
 * It also handles a toolbox containing a list of actions.
 * 
 * The toolbox is available when either of the two layers is active. Otherwise
 * the toolbox is hidden.
 * 
 * All actions are restricted to the two layers belonging to the workingSet.
 * 
 * The data in the ODS layer is retrieved from 1 or more ODS dataSources.
 * 
 * @author Gertjan Idema
 *
 */
public class OdsWorkingSet implements LayerChangeListener, FeatureListener {
  private String name;
  private final Map<String, OdsDataSource> dataSources = new HashMap<String, OdsDataSource>();
  OdsDataLayer odsDataLayer;
  OdsOsmDataLayer odsOsmDataLayer;
  private JDialog toolbox;
  private final List<OdsAction> actions = new LinkedList<OdsAction>();
  String osmQuery;
  private FeatureStore featureStore;
  private final Map<OsmPrimitive, Feature> relatedFeatures = new HashMap<OsmPrimitive, Feature>();
  OdsDownloadAction downloadAction;
  private boolean active = false;

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
     * Currently, we pass the osm (overpass) query through http get. This
     * doesn't allow linefeed or carriage return characters, so we need to strip
     * them.
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
  
  void activate() {
    if (!active) {
      downloadAction = new OdsDownloadAction();
      downloadAction.setWorkingSet(this);
      initToolbox();
      getOdsDataLayer();
      getOdsOsmDataLayer();
      active = true;
    }
  }
  
  private void deActivate() {
    odsOsmDataLayer = null;
    odsDataLayer = null;
    toolbox.setVisible(false);
    toolbox = null;
    active = false;
  }
 
  public JDialog getToolbox() {
    return toolbox;
  }
  
  private void initToolbox() {
    toolbox = new JDialog((Frame) Main.main.parent, "ODS");
    toolbox.setLayout(new BoxLayout(toolbox.getContentPane(),
      BoxLayout.Y_AXIS));
    toolbox.setLocation(300, 300);
    toolbox.setMinimumSize(new Dimension(110, 0));
    toolbox.add(new JButton(downloadAction));
    for (Action action : actions) {
      toolbox.add(new JButton(action));
    }
    toolbox.pack();
  }

  public void download(Bounds area, boolean downloadOsmData) throws ExecutionException, InterruptedException {
    OdsDownloader downloader = new OdsDownloader(this);
    downloader.download(area);
  }

  void activateOsmLayer() {
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

//  /**
//   * @param featureSet
//   * @param boundingBox
//   */
//  public void addFeatures(OdsFeatureSet featureSet, Bounds boundingBox) {
//    DataSet dataSet = getOdsDataLayer().data;
//    dataSet.beginUpdate();
//    for (SimpleFeature feature : featureSet.getFeatures()) {
//      getFeatureStore().addFeature(feature);
//    }
//    dataSet.endUpdate();
//    dataSet.dataSources.add(new DataSource(boundingBox, name));
//  }

  @Override
  public void featuresAdded(List<SimpleFeature> newFeatures, Bounds bounds) {
    DataSet dataSet = getOdsDataLayer().data;
    dataSet.beginUpdate();
    for (SimpleFeature feature : newFeatures) {
      featureAdded(feature);
    }
    dataSet.endUpdate();
    dataSet.dataSources.add(new DataSource(bounds, name));
  }

  // Implement FeatureListener
  @Override
  public void featureAdded(Feature feature) {
    String typeName = feature.getName().getLocalPart();
    OdsDataSource ds = dataSources.get(typeName);
    FeatureMapper mapper = ds.getFeatureMapper();
    List<OsmPrimitive> primitives = mapper.mapFeature(feature,
        getOdsDataLayer().data);
    for (OsmPrimitive primitive : primitives) {
      relatedFeatures.put(primitive, feature);
    }
  }

  // Implement LayerChangeListener

  @Override
  public void activeLayerChange(Layer oldLayer, Layer newLayer) {
    if (newLayer != null && (newLayer == odsDataLayer || newLayer == odsOsmDataLayer)) {
      getToolbox().setVisible(true);
    }
    else if (active) {
      getToolbox().setVisible(false);
    }
  }

  @Override
  public void layerAdded(Layer newLayer) {
    // No action required
  }

  @Override
  public void layerRemoved(Layer oldLayer) {
    boolean deActivate = false;
    if (oldLayer == odsDataLayer && Main.map.mapView.getAllLayers().contains(odsOsmDataLayer)) {
      Main.map.mapView.removeLayer(odsOsmDataLayer);
      deActivate = true;
    } else if (oldLayer == odsOsmDataLayer && Main.map.mapView.getAllLayers().contains(odsDataLayer)) {
      Main.map.mapView.removeLayer(odsDataLayer);
      deActivate = true;
    }
    if (deActivate) {
      deActivate();
    }
  }
  
  public Action getActivateAction() {
    return new ActivateAction();
  }
  
  public OdsDownloadAction getDownloadAction() {
    return downloadAction;
  }

//  private FeatureStore getFeatureStore() {
//    if (featureStore == null) {
//      featureStore = new FeatureStore();
//      featureStore.addFeatureListener(this);
//      for (OdsDataSource dataSource : dataSources.values()) {
//        featureStore.addIdFactory(dataSource.getIdFactory());
//      }
//    }
//    return featureStore;
//  }
  
  private class ActivateAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
      activate();
      downloadAction.actionPerformed(e);
    }
  }
}
