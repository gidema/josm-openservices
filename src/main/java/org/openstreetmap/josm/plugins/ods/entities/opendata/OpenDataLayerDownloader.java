package org.openstreetmap.josm.plugins.ods.entities.opendata;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.io.DownloadObserver;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.io.Downloader;
import org.openstreetmap.josm.plugins.ods.io.LayerDownloader;
import org.openstreetmap.josm.plugins.ods.io.Status;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;

// TODO decide upon and document Class lifecycle
public class OpenDataLayerDownloader implements LayerDownloader {
    private static final int NTHREADS = 10;

    private final OdLayerManager odLayerManager;
    private final List<? extends FeatureDownloader> downloaders;
    private final List<Runnable> odProcessors;
    private final OdBoundaryManager boundaryManager;
    private Status status = new Status();
    private DownloadRequest request;
    private DownloadResponse response;


    private ExecutorService executor;

    public OpenDataLayerDownloader(OdLayerManager odLayerManager,
            List<? extends FeatureDownloader> downloaders,
            List<Runnable> odProcessors,
            OdBoundaryManager boundaryManager) {
        this.odLayerManager = odLayerManager;
        this.downloaders = downloaders;
        this.odProcessors = odProcessors;
        this.boundaryManager = boundaryManager;
    }

    @Override
    public void setResponse(DownloadResponse response) {
        this.response = response;
    }

    @Override
    public void setup(DownloadRequest request) {
        this.request = request;
        for (FeatureDownloader downloader : downloaders) {
            downloader.setup(request);
        }
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void prepare() {
        executor = Executors.newFixedThreadPool(NTHREADS);
        status.clear();
        for (final Downloader downloader : downloaders) {
            executor.execute(downloader::prepare);
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                // Timeout occurred
                status.setTimedOut(true);
            }
        }
        catch (InterruptedException e) {
            executor.shutdownNow();
            for (Downloader downloader : downloaders) {
                downloader.cancel();
            }
            status.setCancelled(true);
            status.setException(e);
            return;
        }
    }

    @Override
    public void download() {
        executor = Executors.newFixedThreadPool(NTHREADS);
        status.clear();
        for (final Downloader downloader : downloaders) {
            executor.execute(downloader::download);
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                for (Downloader downloader : downloaders) {
                    downloader.cancel();
                }
                status.setTimedOut(true);
                return;
            }
        }
        catch (InterruptedException e) {
            executor.shutdownNow();
            for (Downloader downloader : downloaders) {
                downloader.cancel();
            }
            status.setCancelled(true);
            status.setException(e);
            return;
        }
        for (Downloader downloader : downloaders) {
            Status childStatus = downloader.getStatus();
            if (!childStatus.isSucces()) {
                this.status = childStatus;
                break;
            }
        }
        this.response = new DownloadResponse(request);
    }

    @Override
    public void process() {
        parseFeatures();
        boundaryManager.update(getResponse().getRequest().getBoundary().getMultiPolygon());
        //        executor = Executors.newFixedThreadPool(NTHREADS);
        status.clear();

        for (final Runnable odProcessor : odProcessors) {
            if (odProcessor instanceof DownloadObserver) {
                ((DownloadObserver)odProcessor).downloadFinished(response);
                //                executor.execute(odProcessor);
            }
            odProcessor.run();
        }

        //        executor.shutdown();
        //        try {
        //            executor.awaitTermination(1, TimeUnit.MINUTES);
        //        }
        //        catch (Exception e) {
        //            executor.shutdownNow();
        //            for (FeatureDownloader downloader : downloaders) {
        //                downloader.cancel();
        //            }
        //            status.setException(e);
        //            return;
        //        }
        Boundary boundary = request.getBoundary();
        DataSource ds = new DataSource(boundary.getBounds(), "Import");
        OsmDataLayer osmDataLayer = odLayerManager.getOsmDataLayer();
        osmDataLayer.getDataSet().addDataSource(ds);
    }

    private void parseFeatures() {
        // Consider parallelization
        for (FeatureDownloader downloader : downloaders) {
            downloader.process();
        }
    }

    public DownloadResponse getResponse() {
        return response;
    }

    @Override
    public void cancel() {
        this.status.setCancelled(true);
        for (FeatureDownloader downloader : downloaders) {
            downloader.cancel();
        }
        executor.shutdownNow();
    }
}
