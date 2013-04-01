package org.openstreetmap.josm.plugins.openservices;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
  private OdsDownloadAction downloadAction;

  public OdsWorkingSet() {
    MapView.addLayerChangeListener(this);
    addAction(getDownloadAction());
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
  }

  public JDialog getToolbox() {
    if (toolbox == null) {
      toolbox = new JDialog((Frame) Main.main.parent, "ODS");
      toolbox.setLayout(new BoxLayout(toolbox.getContentPane(),
          BoxLayout.Y_AXIS));
      toolbox.setLocation(300, 300);
      toolbox.setMinimumSize(new Dimension(110, 0));
      for (Action action : actions) {
        toolbox.add(new JButton(action));
      }
      toolbox.pack();
    }
    return toolbox;
  }

  public void download(Bounds area, boolean downloadOsmData) {
    OdsWorkingSetDownloader downloader = new OdsWorkingSetDownloader(this,
        area, downloadOsmData);
    downloader.download();
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

  public void addFeatures(Collection<SimpleFeature> features, Bounds boundingBox) {
    DataSet dataSet = getOdsDataLayer().data;
    dataSet.beginUpdate();
    for (SimpleFeature feature : features) {
      getFeatureStore().addFeature(feature);
    }
    dataSet.endUpdate();
    dataSet.dataSources.add(new DataSource(boundingBox, name));
  }

  @Override
  public void featuresAdded(List<SimpleFeature> newFeatures, Bounds bounds) {
    // TODO Auto-generated method stub

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

  public OdsDownloadAction getDownloadAction() {
    if (downloadAction == null) {
      downloadAction = new OdsDownloadAction();
      downloadAction.setWorkingSet(this);
    }
    return downloadAction;
  }

  private FeatureStore getFeatureStore() {
    if (featureStore == null) {
      featureStore = new FeatureStore();
      featureStore.addFeatureListener(this);
      for (OdsDataSource dataSource : dataSources.values()) {
        featureStore.addIdFactory(dataSource.getIdFactory());
      }
    }
    return featureStore;
  }
}
