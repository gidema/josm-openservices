package org.openstreetmap.josm.plugins.openservices.wfs;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.io.IOException;
import java.util.concurrent.Future;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.downloadtasks.AbstractDownloadTask;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.gui.PleaseWaitRunnable;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.io.OsmTransferCanceledException;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.openservices.BBoxUtil;
import org.openstreetmap.josm.plugins.openservices.DataSource;
import org.xml.sax.SAXException;

public class WFSDownloadTask extends AbstractDownloadTask {
  protected Bounds currentBounds;
  private DownloadTask downloadTask;
  WFSService service;
  DataSource dataSource;

  public WFSDownloadTask(WFSService service, DataSource dataSource) {
    super();
    this.service = service;
    this.dataSource = dataSource;
  }

  @Override
  public Future<?> download(boolean newLayer, Bounds downloadArea,
      ProgressMonitor progressMonitor) {
    return download(new DownloadTask(progressMonitor), downloadArea);
  }
  
  protected Future<?> download(DownloadTask dlTask, Bounds downloadArea) {
    this.downloadTask = dlTask;
    this.currentBounds = new Bounds(downloadArea);
    // We need submit instead of execute so we can wait for it to finish and get the error
    // message if necessary. If no one calls getErrorMessage() it just behaves like execute.
    return Main.worker.submit(downloadTask);
  }

  @Override
  public Future<?> loadUrl(boolean newLayer, String url,
      ProgressMonitor progressMonitor) {
    throw new UnsupportedOperationException();
  }

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

  class DownloadTask extends PleaseWaitRunnable {
    private FeatureCollection<?, ?> features;
    
    public DownloadTask(ProgressMonitor progressMonitor) {
      super(tr("Downloading data"), progressMonitor, false);
    }

    @Override protected void cancel() {
      setCanceled(true);
    }

    @Override public void realRun() throws IOException, SAXException, OsmTransferException {
      try {
          if (isCanceled())
              return;
          ProgressMonitor subTaskMonitor = progressMonitor.createSubTaskMonitor(ProgressMonitor.ALL_TICKS, false);
          subTaskMonitor.beginTask(tr("Contacting Server..."), 10);
          FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
          service.init();
          // Find faster solution for the following line
          ReferencedEnvelope bbox = BBoxUtil.createBoundingBox(service.getCrs(), currentBounds);
          Filter filter = ff.bbox(ff.property(""), bbox);
          features = service.getFeatureSource().getFeatures(filter);
      } catch(Exception e) {
          if (isCanceled()) {
              System.out.println(tr("Ignoring exception because download has been canceled. Exception was: {0}", e.toString()));
              return;
          }
          if (e instanceof OsmTransferCanceledException) {
              setCanceled(true);
              return;
          } else if (e instanceof OsmTransferException) {
              rememberException(e);
          } else {
              rememberException(new OsmTransferException(e));
          }
          setFailed(true);
      }
  }


    @Override
    protected void finish() {
      if (features.size() == 0) {
        rememberErrorMessage(tr("No data found in this area."));
      }
      dataSource.addFeatures(features, service);
    }
  }
}
