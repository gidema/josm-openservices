package org.openstreetmap.josm.plugins.ods.io;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.tools.I18n;

/**
 * Main downloader that retrieves data from multiple sources. Currently only a OSM source
 * and a single OpenData source are supported.
 * The 
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public abstract class MainDownloader {
    private static final int NTHREADS = 10;

    private List<LayerDownloader> enabledDownloaders;
    
    private ExecutorService executor;

    private Status status = new Status();

    protected abstract LayerDownloader getOsmLayerDownloader();

    protected abstract LayerDownloader getOpenDataLayerDownloader();

    public void run(ProgressMonitor pm, DownloadRequest request) throws ExecutionException, InterruptedException {
        status.clear();
        pm.indeterminateSubTask(I18n.tr("Setup"));
        setup(request);
        pm.indeterminateSubTask(I18n.tr("Preparing"));
        prepare();
        pm.indeterminateSubTask(I18n.tr("Downloading"));
        download();
        if (!status.isSucces()) {
            pm.finishTask();
            JOptionPane.showMessageDialog(Main.parent, 
                "An error occurred: " + status.getMessage());
            return;
        }
        pm.indeterminateSubTask(I18n.tr("Processing data"));
        DownloadResponse response = new DownloadResponse(request);
        process(response);
        if (!status.isSucces()) {
            pm.finishTask();
            JOptionPane.showMessageDialog(Main.parent, 
                    "An error occurred: " + status.getMessage());
            return;
        }
        
        Bounds bounds = request.getBoundary().getBounds();
        computeBboxAndCenterScale(bounds);
        pm.finishTask();
    }

    /**
     * Setup the download jobs. One job for the Osm data and one for imported data.
     * Setup the download tasks. Maybe more than 1 per job. 
     */
    private void setup(DownloadRequest request) {
        status.clear();
        enabledDownloaders = new LinkedList<LayerDownloader>();
        if (request.isGetOsm()) {
            enabledDownloaders.add(getOsmLayerDownloader());
        }
        if (request.isGetOds()) {
            enabledDownloaders.add(getOpenDataLayerDownloader());
        }
        for (LayerDownloader downloader : enabledDownloaders) {
            downloader.setup(request);
        }
    }

    private void prepare() {
        status.clear();
        executor = Executors.newFixedThreadPool(NTHREADS);
        for (final LayerDownloader downloader : enabledDownloaders) {
            executor.execute(downloader::prepare);
        }
        
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException e) {
            executor.shutdownNow();
            for (LayerDownloader downloader : enabledDownloaders) {
                downloader.cancel();
            }
            status.setException(e);
            status.setFailed(true);
        }
        for (LayerDownloader downloader : enabledDownloaders) {
            Status status = downloader.getStatus();
            if (!status.isSucces()) {
                this.status = status;
            }
        }
    }

    private void download() {
        status.clear();
        executor = Executors.newFixedThreadPool(NTHREADS);
        for (final LayerDownloader downloader : enabledDownloaders) {
            executor.execute(downloader::download);
        }
        
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException e) {
            executor.shutdownNow();
            for (LayerDownloader downloader : enabledDownloaders) {
                downloader.cancel();
            }
            status.setException(e);
            status.setFailed(true);
        }
        for (LayerDownloader downloader : enabledDownloaders) {
            Status status = downloader.getStatus();
            if (!status.isSucces()) {
                this.status = status;
            }
        }
    }
    
    /**
     * Run the tasks that depend on more than one entity store.
     * 
     */
    protected void process(DownloadResponse response) {
        status.clear();
        executor = Executors.newFixedThreadPool(NTHREADS);
        for (final LayerDownloader downloader : enabledDownloaders) {
            downloader.setResponse(response);
            executor.execute(downloader::process);
        }
        
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException e) {
            executor.shutdownNow();
            for (LayerDownloader downloader : enabledDownloaders) {
                downloader.cancel();
            }
            status.setException(e);
            status.setFailed(true);
        }
        for (LayerDownloader downloader : enabledDownloaders) {
            Status status = downloader.getStatus();
            if (!status.isSucces()) {
                this.status = status;
            }
        }
    }

    protected void computeBboxAndCenterScale(Bounds bounds) {
        BoundingXYVisitor v = new BoundingXYVisitor();
        if (bounds != null) {
            v.visit(bounds);
            Main.map.mapView.zoomTo(bounds);
        }
    }

    public void cancel() {
        status.setCancelled(true);
        for (LayerDownloader downloader : enabledDownloaders) {
            downloader.cancel();
        }
    }
}