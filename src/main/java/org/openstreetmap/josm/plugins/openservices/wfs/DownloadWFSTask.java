// License: GPL. Copyright 2007 by Immanuel Scholz and others
package org.openstreetmap.josm.plugins.openservices.wfs;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Future;

import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
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
import org.openstreetmap.josm.plugins.openservices.crs.JTSCoordinateTransform;
import org.openstreetmap.josm.plugins.openservices.crs.JTSCoordinateTransformFactory;
import org.openstreetmap.josm.plugins.openservices.crs.Proj4jCRSUtilFactory;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Task allowing to download GPS data.
 */
public abstract class DownloadWFSTask extends CustomDownloadTask {
  private static final int JOSM_SRID = 4326; 
  private final static JTSCoordinateTransformFactory crsTransformFactory = new Proj4jCRSUtilFactory();

  protected Bounds currentBounds;
  private DownloadTask downloadTask;
  DataSet downloadedData;
  
  private WfsHost wfsHost;
  private String wfsFeature;
  private CustomDataLayer targetLayer;

  /**
   * Create a new WFSDownload task with the given wfshost and dataSetManager
   * @param wfsHost
   * @param dataSetManager
   */
  public DownloadWFSTask(WfsHost wfsHost) {
    super();
    this.wfsHost = wfsHost;
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
    downloadTask = new DownloadTask(newLayer, progressMonitor);
    // We need submit instead of execute so we can wait for it to finish and get
    // the error
    // message if necessary. If no one calls getErrorMessage() it just behaves
    // like execute.
    return Main.worker.submit(downloadTask);
  }

  @Override
  public Future<?> loadUrl(boolean newLayer, String url,
      ProgressMonitor progressMonitor) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
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
  
  /**
   * @param wfsHost the wfsHost to set
   */
  protected void setWfsHost(WfsHost wfsHost) {
    this.wfsHost = wfsHost;
  }

  /**
   * @param wfsFeature the wfsFeature to set
   */
  public void setWfsFeature(String wfsFeature) {
    this.wfsFeature = wfsFeature;
  }
  
  /**
   * Create a ReferencedEnvelope from a Josm bounds object, using the supplied CoordinateReferenceSystem
   * 
   * @param crs
   * @param bounds
   * @return
   */
  private static ReferencedEnvelope createBoundingBox(CoordinateReferenceSystem crs, Bounds bounds) {
    String crsCode = crs.getIdentifiers().toArray(new ReferenceIdentifier[0])[0].getCode();
    int targetSRID = Integer.parseInt(crsCode);
    JTSCoordinateTransform transform = crsTransformFactory.createJTSCoordinateTransform(JOSM_SRID, targetSRID);

    Coordinate min = getCoordinate(bounds.getMin());
    Coordinate max = getCoordinate(bounds.getMax());
    Coordinate targetMin = transform.transform(min);
    Coordinate targetMax = transform.transform(max);
    return new ReferencedEnvelope(targetMin.x, targetMax.x,
      targetMin.y, targetMax.y, crs);
  }
  
  private static Coordinate getCoordinate(LatLon ll) {
    return new Coordinate(ll.getX(), ll.getY());
  }


  class DownloadTask extends PleaseWaitRunnable {
    @SuppressWarnings("unused")
    private final boolean newLayer;
    private SimpleFeatureCollection features;
    
    public DownloadTask(boolean newLayer, ProgressMonitor progressMonitor) {
      super(tr("Downloading data"), progressMonitor, false);
      this.newLayer = newLayer;
    }

    @SuppressWarnings("synthetic-access")
    @Override
    public void realRun() throws IOException, SAXException,
        OsmTransferException {
      try {
        if (isCanceled())
          return;
        wfsHost.init();
        DataStore dataStore = wfsHost.getDataStore();
        SimpleFeatureSource featureSource = dataStore.getFeatureSource(wfsFeature);
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
        // A bit complex way to find the CRS code, but at least it works.
        CoordinateReferenceSystem crs = featureSource.getInfo().getCRS();
        ReferencedEnvelope bbox = createBoundingBox(crs, currentBounds);
        Filter filter = ff.bbox(ff.property(""), bbox);
        features = featureSource.getFeatures(filter);
        //featureSource.getInfo().
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
        } else if (e instanceof WfsException) {
          rememberException(e);
        } else {
          rememberException(new WfsException(e));
        }
        DownloadWFSTask.this.setFailed(true);
      }
    }


    @SuppressWarnings("synthetic-access")
    @Override
    protected void finish() {
      if (isFailed() || isCanceled())
        return;
      if (features == null)
        return; // user canceled download or error occurred
      if (features.isEmpty()) {
        rememberErrorMessage(tr("No data found in this area."));
        // TODO (see DownloadOsmTask)
        // need to synthesize a download bounds lest the visual indication of
        // downloaded
        // area doesn't work
      }

      targetLayer = getTargetLayer();
      WFSDataSet<?> dataSet = (WFSDataSet<?>) targetLayer.data;
      dataSet.addFeatures(features);
      rememberDownloadedData(dataSet);
      computeBboxAndCenterScale();
      dataSet.dataSources.add(new DataSource(
        currentBounds != null ? currentBounds
        : new Bounds(new LatLon(0, 0)), "OpenStreetMap server"));

//      // If the mapView is not there yet, we cannot calculate the bounds (see
//      // constructor of MapView).
//      // Otherwise jump to the current download.
//      if (isDisplayingMapView) {
//        Main.map.mapView.setActiveLayer(targetLayer);
//        computeBboxAndCenterScale();
//      }
    }

    protected CustomDataLayer getTargetLayer() {
      if (Main.isDisplayingMapView()) {
        Collection<Layer> layers = Main.map.mapView.getAllLayers();
        for (Layer layer : layers) {
          if (layer instanceof CustomDataLayer 
              && layer.getName() == getLayerName() ){
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
