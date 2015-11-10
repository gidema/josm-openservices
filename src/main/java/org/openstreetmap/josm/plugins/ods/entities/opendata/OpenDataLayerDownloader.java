package org.openstreetmap.josm.plugins.ods.entities.opendata;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.io.Downloader;
import org.openstreetmap.josm.plugins.ods.io.LayerDownloader;
import org.openstreetmap.josm.plugins.ods.io.Status;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.tasks.Task;

// TODO decide upon and document Class lifecycle
public class OpenDataLayerDownloader implements LayerDownloader {
    private static final int NTHREADS = 10;

    private final OdsModule module;
    private final List<FeatureDownloader> downloaders;
    private final List<Task> tasks;
    private Status status = new Status();
    private DownloadRequest request;
    private DownloadResponse response;

    private ExecutorService executor;

    @Deprecated
    public OpenDataLayerDownloader(OdsModule module, List<FeatureDownloader> downloaders, List<Task> tasks) {
        this.module = module;
        this.downloaders = downloaders;
        this.tasks = (tasks == null ? new ArrayList<>(0) : tasks);
    }
    
    public OpenDataLayerDownloader(OdsModule module) {
        this.module = module;
        this.downloaders = new LinkedList<>();
        this.tasks = new LinkedList<>();
    }
    
    @Override
    public void setResponse(DownloadResponse response) {
        this.response = response;
    }

    protected void addFeatureDownloader(FeatureDownloader featureDownloader) {
        this.downloaders.add(featureDownloader);
    }
//   
//    protected List<? extends FeatureDownloader> getDownloaders() {
//        return downloaders;
//    }
//
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
        long millis = System.currentTimeMillis();
        for (Task task : tasks) {
//            task.run(ctx);
            Main.info("Task: {0} = {1} ms;", task.getClass().getSimpleName(),
                System.currentTimeMillis() - millis);
            millis = System.currentTimeMillis();
        }
        Boundary boundary = request.getBoundary();
        DataSource ds = new DataSource(boundary.getBounds(), "Import");
        OsmDataLayer osmDataLayer = module.getOpenDataLayerManager().getOsmDataLayer();
        osmDataLayer.data.dataSources.add(ds);
    }

    public DownloadResponse getResponse() {
        return response;
    }

    @Override
    public void cancel() {
        executor.shutdownNow();
        this.status.setCancelled(true);
    }
}
