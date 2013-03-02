package org.openstreetmap.josm.plugins.openservices;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.concurrent.Future;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.downloadtasks.DownloadOsmTask;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.openservices.wfs.WFSDownloadTask;

public class DownloadAction extends AbstractAction {
  private final boolean enabled = true;
  private ServiceLayer layer;

  public DownloadAction() {
    super();
    //super(name, iconName, toolTip, null, false, toolbarId, false);
    //putValue("help", helpTopic);
  }

  public void setName(String name) {
    this.putValue(Action.NAME, name);
    this.putValue("toolbar", name);
  }
  
  public final void setLayer(ServiceLayer layer) {
    this.layer = layer;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    DownloadDialog dialog = DownloadDialog.getInstance();
    dialog.restoreSettings();
    dialog.setVisible(true);
    if (dialog.isCanceled()) {
      return;
    }
    dialog.rememberSettings();
    Bounds area = dialog.getSelectedDownloadArea();
    
    Layer activeLayer = null;
    if (Main.isDisplayingMapView()) {
      activeLayer = Main.map.mapView.getActiveLayer();
    }
    Future<?> future1 = null;
    DownloadOsmTask task;
    if (dialog.isDownloadOsmData()) {
      activateOsmLayer();
      String osmQuery = layer.getOsmQuery();
      if (osmQuery != null) {
        String url = getOverpassUrl(osmQuery, area);
        task = new DownloadOsmTask();
        future1 = task.loadUrl(dialog.isNewLayerRequired(), url, null);
      }
      else {
        task = new DownloadOsmTask();
        future1 = task.download(dialog.isNewLayerRequired(), area, null);
      }
      Main.worker.submit(new org.openstreetmap.josm.actions.downloadtasks.PostDownloadHandler(task, future1));
    }
    for (WFSDownloadTask downloadTask : layer.getDataSource().getTasks()) {
      Future<?> future2 = downloadTask.download(false, area, null);
      Main.worker.submit(new PostDownloadHandler(future2));
    }
  }
  
  private void activateOsmLayer() {
    Layer osmLayer = getOsmLayer();
    Main.map.mapView.setActiveLayer(osmLayer);
  }
  
  private static Layer getOsmLayer() {
    if (Main.isDisplayingMapView()) {
      Collection<Layer> layers = Main.map.mapView.getAllLayers();
      for (Layer osmLayer : layers) {
        if (osmLayer.getName().equals("osmData")) {
          return osmLayer;
        }
      }
    }
    Layer osmLayer = new OsmDataLayer(new DataSet(), "osmData", null);
    Main.main.addLayer(osmLayer);
    return osmLayer;
  }
  
  private static String getOverpassUrl(String query, Bounds bounds) {
    String host = "http://overpass-api.de/api";
    return String.format("%s/interpreter?data=node(%f,%f,%f,%f);%s;out meta;", 
      host, bounds.getMin().getY(), bounds.getMin().getX(), 
      bounds.getMax().getY(), bounds.getMax().getX(), query);
  }
}
