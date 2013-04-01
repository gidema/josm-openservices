package org.openstreetmap.josm.plugins.openservices;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Future;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.downloadtasks.AbstractDownloadTask;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.gui.PleaseWaitRunnable;
import org.openstreetmap.josm.gui.progress.PleaseWaitProgressMonitor;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.io.OsmTransferCanceledException;
import org.openstreetmap.josm.io.OsmTransferException;
import org.xml.sax.SAXException;

public abstract class OdsDownloadTask extends AbstractDownloadTask {
  protected Bounds currentBounds;
  private DownloadTask downloadTask;
  protected OdsDataSource dataSource;
  private final Collection<SimpleFeature> featureCollection;

  public OdsDownloadTask(OdsDataSource dataSource, Collection<SimpleFeature> featureCollection) {
    super();
    this.dataSource = dataSource;
    this.featureCollection = featureCollection;
  }

  @Override
  public Future<?> download(boolean newLayer, Bounds downloadArea,
      ProgressMonitor progressMonitor) {
    ProgressMonitor theProgressMonitor = progressMonitor;
    if (theProgressMonitor == null) {
      theProgressMonitor = new PleaseWaitProgressMonitor(
        tr("Downloading {0} Data...", dataSource.getService().getFeatureName()));
    }
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

  abstract protected FeatureCollection<?, SimpleFeature> getFeatures() throws ServiceException;
  
  class DownloadTask extends PleaseWaitRunnable {    
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
          FeatureCollection<?, SimpleFeature> features = getFeatures();
          subTaskMonitor.worked(1);
          subTaskMonitor.indeterminateSubTask(tr("Downloading data..."));
          if (features == null) {
            // TODO Do something
          } else {
            FeatureIterator<SimpleFeature> it = features.features();
            while (it.hasNext()) {
              featureCollection.add(it.next());
            }
          }
          subTaskMonitor.worked(9);
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
              e.printStackTrace();
          } else {
              rememberException(new OsmTransferException(e));
              e.printStackTrace();
          }
          System.out.println("failed");
          OdsDownloadTask.this.setFailed(true);
      }
    }

    @Override
    protected void finish() {
      if (isFailed()) {
        return;
      }
    }
  }
}
