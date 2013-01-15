// License: GPL. Copyright 2007 by Immanuel Scholz and others
package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Future;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.DataSource;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.PleaseWaitRunnable;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.io.OsmTransferCanceledException;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.openservices.CustomDataLayer;
import org.openstreetmap.josm.plugins.openservices.CustomDownloadTask;
import org.xml.sax.SAXException;

/**
 * Task allowing to download GPS data.
 */
public abstract class DownloadArcgisRestTask extends CustomDownloadTask {
  private DownloadTask downloadTask;
  DataSet downloadedData;
  Bounds currentBounds;

  CustomDataLayer targetLayer;

  public DownloadArcgisRestTask() {
    super();
  }

  protected void rememberDownloadedData(DataSet ds) {
    this.downloadedData = ds;
  }

  /**
   * Replies the {@link DataSet} containing the downloaded OSM data.
   * 
   * @return The {@link DataSet} containing the downloaded OSM data.
   */
  public DataSet getDownloadedData() {
    return downloadedData;
  }

  @Override
  public Future<?> download(boolean newLayer, Bounds downloadArea,
      ProgressMonitor progressMonitor) {
    currentBounds = downloadArea;
    RestQuery query = getQuery(currentBounds);
    query.setInSR(4326L);
    query.setGeometry(formatBounds(currentBounds));
 
    downloadTask = new DownloadTask(query, progressMonitor);
    // We need submit instead of execute so we can wait for it to finish and get
    // the error
    // message if necessary. If no one calls getErrorMessage() it just behaves
    // like execute.
    return Main.worker.submit(downloadTask);
  }

  protected abstract RestQuery getQuery(Bounds bounds);

  
  /**
   * This method must be implemented as it is abstract in the
   * parent class, but it is not used.
   * 
   * @see org.openstreetmap.josm.actions.downloadtasks.DownloadTask#loadUrl(boolean, java.lang.String, org.openstreetmap.josm.gui.progress.ProgressMonitor)
   */
  @Override
  public Future<?> loadUrl(boolean newLayer, String url,
      ProgressMonitor progressMonitor) {
    throw new UnsupportedOperationException();
  }

  /**
   * This method must be implemented as it is abstract in the
   * parent class, but it is not used.
   * 
   * @see
   * org.openstreetmap.josm.actions.downloadtasks.DownloadTask#acceptsUrl(java
   * .lang.String)
   */
  @Override
  public boolean acceptsUrl(String url) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void cancel() {
    if (downloadTask != null) {
      downloadTask.cancel();
    }
  }
  
  private String formatBounds(Bounds bounds) {
    return String.format("%f,%f,%f,%f",
        bounds.getMin().getX(), bounds.getMin().getY(),
        bounds.getMax().getX(), bounds.getMax().getY());
  }

  class DownloadTask extends PleaseWaitRunnable {
    private final RestQuery query;
    private JSONObject downloadedJSON;
    private JSONArray features;
    private ArcgisRestLayer layer;

    public DownloadTask(RestQuery query,
        ProgressMonitor progressMonitor) {
      super(tr("Downloading data"), progressMonitor, false);
      this.query = query;
    }

    @Override
    public void realRun() throws IOException, SAXException,
        OsmTransferException {
      try {
        if (isCanceled())
          return;
        ARReader reader = new ARReader(query);
        downloadedJSON = reader.getJson(progressMonitor.createSubTaskMonitor(10, true));
        layer = reader.getLayer();
      } catch (Exception e) {
        if (isCanceled()) {
          System.out
              .println(tr(
                  "Ignoring exception because download has been canceled. Exception was: {0}",
                  e.toString()));
          return;
        }
        if (e instanceof OsmTransferCanceledException) {
          setCanceled(true);
          return;
        } else if (e instanceof ArcgisServerRestException) {
          rememberException(e);
        } else {
          rememberException(new ArcgisServerRestException(e));
        }
        DownloadArcgisRestTask.this.setFailed(true);
      }
    }

    @Override
    protected void finish() {
      if (isFailed() || isCanceled())
        return;
//      if (dataSet == null)
//        return; // user canceled download or error occurred
      // Check for errors
      JSONObject errorObject = (JSONObject) downloadedJSON.get("error");
      if (errorObject != null) {
        setFailed(true);
        rememberErrorMessage(errorObject.toString());
        return;
      }
      features = (JSONArray) downloadedJSON.get("features");
      targetLayer = getTargetLayer();
      ArggisRestDataSet<?> dataSet = (ArggisRestDataSet<?>) targetLayer.data;
      if (features.size() == 0) {
        rememberErrorMessage(tr("No data found in this area."));
        // need to synthesize a download bounds lest the visual indication of
        // downloaded
        // area doesn't work
        dataSet.dataSources.add(new DataSource(
            currentBounds != null ? currentBounds
                : new Bounds(new LatLon(0, 0)), "OpenStreetMap server"));
      }

      dataSet.addFeatures(layer, features);
      rememberDownloadedData(dataSet);
      // Allways create a new layer
      final boolean isDisplayingMapView = Main.isDisplayingMapView();

      //Main.main.addLayer(targetLayer);

      // If the mapView is not there yet, we cannot calculate the bounds (see
      // constructor of MapView).
      // Otherwise jump to the current download.
      if (isDisplayingMapView) {
        computeBboxAndCenterScale();
      }
    }

    protected CustomDataLayer getTargetLayer() {
      if (Main.isDisplayingMapView()) {
        Collection<Layer> layers = Main.map.mapView.getAllLayers();
        for (Layer layer : layers) {
          if (layer instanceof CustomDataLayer 
              && layer.getName().equals(getLayerName())){
            return (CustomDataLayer) layer;
          }
        }
      }
      CustomDataLayer layer = createTargetLayer(getLayerName());
      Main.main.addLayer(layer);
      return layer;
    }

    protected void computeBboxAndCenterScale() {
      BoundingXYVisitor v = new BoundingXYVisitor();
      if (currentBounds != null) {
        v.visit(currentBounds);
      } else {
        v.computeBoundingBox(downloadedData.getNodes());
      }
      Main.map.mapView.recalculateCenterScale(v);
    }

    @Override
    protected void cancel() {
      setCanceled(true);
    }
  }
}
