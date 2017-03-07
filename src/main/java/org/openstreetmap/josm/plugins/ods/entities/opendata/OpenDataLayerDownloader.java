package org.openstreetmap.josm.plugins.ods.entities.opendata;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.io.Downloader;
import org.openstreetmap.josm.plugins.ods.io.LayerDownloader;
import org.openstreetmap.josm.plugins.ods.io.Status;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;

// TODO decide upon and document Class lifecycle
public class OpenDataLayerDownloader implements LayerDownloader {
    private static final int NTHREADS = 10;

    private final OdsModule module;
    private final List<FeatureDownloader> downloaders;
    private Status status = new Status();
    private DownloadRequest request;
    private DownloadResponse response;

    private ExecutorService executor;

    public OpenDataLayerDownloader(OdsModule module) {
        this.module = module;
        this.downloaders = new LinkedList<>();
    }
    
    @Override
    public void setResponse(DownloadResponse response) {
        this.response = response;
    }

    protected void addFeatureDownloader(FeatureDownloader featureDownloader) {
        this.downloaders.add(featureDownloader);
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
            executor.awaitTermination(1, TimeUnit.MINUTES);
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
        executor = Executors.newFixedThreadPool(NTHREADS);
        status.clear();
        for (final FeatureDownloader downloader : downloaders) {
            downloader.setResponse(response);
            executor.execute(downloader::process);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (Exception e) {
            executor.shutdownNow();
            for (FeatureDownloader downloader : downloaders) {
                downloader.cancel();
            }
            status.setException(e);
            return;
        }
        Boundary boundary = request.getBoundary();
        DataSource ds = new DataSource(boundary.getBounds(), "Import");
        OsmDataLayer osmDataLayer = module.getOpenDataLayerManager().getOsmDataLayer();
        osmDataLayer.data.addDataSource(ds);
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
