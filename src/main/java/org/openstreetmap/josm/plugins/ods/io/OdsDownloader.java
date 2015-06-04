package org.openstreetmap.josm.plugins.ods.io;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.entities.EntitySource;
import org.openstreetmap.josm.plugins.ods.entities.external.GeotoolsDownloadJob;
import org.openstreetmap.josm.plugins.ods.entities.internal.OsmDownloadJob;
import org.openstreetmap.josm.plugins.ods.tasks.Task;
import org.openstreetmap.josm.tools.I18n;

public class OdsDownloader {
    private static final int NTHREADS = 10;

//    private OdsModuleConfig module;
//    private OdsModule module;
    
//    private OsmDownloadJob osmDownloadJob;
//    private GeotoolsDownloadJob geotoolsDownloadJob;
    private boolean downloadOsm;
    private boolean downloadOds;
    
    private List<Downloader> downloaders;
    
//    private ProgressMonitor pm;
    
    private OsmDownloadJob osmDownloadJob;
    private GeotoolsDownloadJob geotoolsDownloadJob;
    
    private List<Task> postDownloadTasks;
    
    private ExecutorService executor;

    private Status status = new Status();
    private Context ctx;
    private EntitySource entitySource;

    public OdsDownloader(OsmDownloadJob osmDownloadJob, 
            GeotoolsDownloadJob geotoolsDownloadJob,
            List<Task> postDownloadTasks) {
        super();
//        this.module = module;
        this.osmDownloadJob = osmDownloadJob;
        this.geotoolsDownloadJob = geotoolsDownloadJob;
        this.postDownloadTasks = postDownloadTasks;
    }

    public void run(ProgressMonitor pm, Context ctx, boolean downloadOsm, boolean downloadOds) throws ExecutionException, InterruptedException {
        status.clear();
        this.ctx = ctx;
        this.entitySource = (EntitySource) ctx.get("entitySource");
        this.downloadOsm = downloadOsm;
        this.downloadOds = downloadOds;
        pm.indeterminateSubTask(I18n.tr("Setup"));
        setup();
        run(Fase.PREPARE);
        if (!status.isSucces()) {
            pm.finishTask();
            return;
        }
        pm.indeterminateSubTask(I18n.tr("Downloading"));
        run(Fase.DOWNLOAD);
        if (!status.isSucces()) {
            pm.finishTask();
            return;
        }
        pm.indeterminateSubTask(I18n.tr("Processing data"));
        run(Fase.PROCESS);
        if (!status.isSucces()) {
            pm.finishTask();
            return;
        }
        process();
        
        Bounds bounds = entitySource.getBoundary().getBounds();
        computeBboxAndCenterScale(bounds);
        pm.finishTask();
    }

    /**
     * Setup the download jobs. One job for the Osm data and one for imported data.
     * Setup the download tasks. Maybe more than 1 per job. 
     */
    private void setup() {
        status.clear();
        downloaders = new LinkedList<Downloader>();
        if (downloadOsm) {
//            osmDownloadJob.setEntitySource(entitySource);
            for (Downloader downloader : osmDownloadJob.getDownloaders()) {
                downloaders.add(downloader);
            }
        }
        if (downloadOds) {
//            geotoolsDownloadJob.setEntitySource(entitySource);
            for (Downloader downloader : geotoolsDownloadJob.getDownloaders()) {
                downloaders.add(downloader);
            }
        }
    }

    private void run(final Fase fase) {
        executor = Executors.newFixedThreadPool(NTHREADS);
        status.clear();
        List<Runnable> tasks = new LinkedList<>();
        for (final Downloader downloader : downloaders) {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    try {
                        switch (fase) {
                        case PREPARE:
                            downloader.prepare(ctx);
                            break;
                        case DOWNLOAD:
                            downloader.download();
                            break;
                        case PROCESS:
                            downloader.process();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        throw new RuntimeException("Unable to run dowload process. " +
                                "This must be a programming error");
                    }
                }
            };
            tasks.add(task);
            executor.execute(task);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException e) {
            executor.shutdownNow();
            for (Downloader downloader : downloaders) {
                downloader.getStatus().setCancelled(true);
            }
        }
        for (Downloader downloader : downloaders) {
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
    private void process() {
        if (downloadOsm) {
            osmDownloadJob.process(ctx);
        }
        if (downloadOds) {
            geotoolsDownloadJob.process(ctx);
        }
        for (Task task : postDownloadTasks) {
            task.run(ctx);
        }
    }

    protected void computeBboxAndCenterScale(Bounds bounds) {
        BoundingXYVisitor v = new BoundingXYVisitor();
        if (bounds != null) {
            v.visit(bounds);
//            Main.map.mapView..recalculateCenterScale(v);
            Main.map.mapView.zoomTo(bounds);
        }
    }

    public void cancel() {
        status.setCancelled(true);
        for (Downloader downloader : downloaders) {
            downloader.cancel();
        }
//        if (executor != null) {
//            System.out.println(executor.isTerminated());
//            executor.shutdownNow();
//            try {
//                executor.awaitTermination(60, TimeUnit.SECONDS);
//                System.out.println(executor.isTerminated());
//            }
//            catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
    
    private enum Fase {
        PREPARE,
        DOWNLOAD,
        PROCESS;
    }
}
