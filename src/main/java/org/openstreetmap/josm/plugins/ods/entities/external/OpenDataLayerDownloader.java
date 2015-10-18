package org.openstreetmap.josm.plugins.ods.entities.external;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;
import org.openstreetmap.josm.plugins.ods.io.LayerDownloader;
import org.openstreetmap.josm.plugins.ods.io.Status;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.tasks.Task;

// TODO decide upon and document Class lifecycle
public class OpenDataLayerDownloader implements LayerDownloader {
    private static final int NTHREADS = 10;

    private final ExternalDataLayer dataLayer;
    private final List<FeatureDownloader> downloaders;
    private final List<Task> tasks;
    private final Status status = new Status();
    private DownloadRequest request;

    private ExecutorService executor;

    public OpenDataLayerDownloader(ExternalDataLayer dataLayer, List<FeatureDownloader> downloaders, List<Task> tasks) {
        this.dataLayer = dataLayer;
        this.downloaders = downloaders;
        this.tasks = (tasks == null ? new ArrayList<>(0) : tasks);
    }
    
    
    protected List<? extends FeatureDownloader> getDownloaders() {
        return downloaders;
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
        for (final FeatureDownloader downloader : downloaders) {
            executor.execute(downloader::prepare);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException e) {
            executor.shutdownNow();
            for (FeatureDownloader downloader : downloaders) {
                downloader.cancel();
            }
        }
    }
    
    @Override
    public void download() {
        executor = Executors.newFixedThreadPool(NTHREADS);
        status.clear();
        for (final FeatureDownloader downloader : downloaders) {
            executor.execute(downloader::download);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException e) {
            executor.shutdownNow();
            for (FeatureDownloader downloader : downloaders) {
                downloader.cancel();
            }
        }
    }
    
    @Override
    public void process() {
        executor = Executors.newFixedThreadPool(NTHREADS);
        status.clear();
        for (final FeatureDownloader downloader : downloaders) {
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
        OsmDataLayer osmDataLayer = dataLayer.getOsmDataLayer();
        osmDataLayer.data.dataSources.add(ds);
    }

    @Override
    public void cancel() {
        executor.shutdownNow();
        this.status.setCancelled(true);
    }
}
