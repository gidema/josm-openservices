package org.openstreetmap.josm.plugins.ods;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.DataSource;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDownloadJob;
import org.openstreetmap.josm.plugins.ods.entities.internal.InternalDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.internal.InternalDownloadJob;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.tools.I18n;

public class OdsDownloader {
    private static final int NTHREADS = 10;

//    private OdsModuleConfig module;
//    private OdsModule module;
    
//    private InternalDownloadJob internalDownloadJob;
//    private ExternalDownloadJob externalDownloadJob;
    private boolean downloadOsm;
    private boolean downloadOds;
    
    private List<DownloadTask> downloadTasks;
    private Boundary boundary;
    
//    private ProgressMonitor pm;
    
    boolean cancelled = false;
    boolean interrupted = false;
    
    private InternalDownloadJob internalDownloadJob;
    private ExternalDownloadJob externalDownloadJob;
    private InternalDataLayer internalDataLayer;
    private ExternalDataLayer externalDataLayer;
    
    private ExecutorService executor;


    @Inject
    public OdsDownloader(InternalDownloadJob internalDownloadJob, 
            ExternalDownloadJob externalDownloadJob,
            ExternalDataLayer externalDataLayer,
            InternalDataLayer internalDataLayer
            ) {
        super();
//        this.module = module;
        this.internalDownloadJob = internalDownloadJob;
        this.externalDownloadJob = externalDownloadJob;
        this.internalDataLayer = internalDataLayer;
        this.externalDataLayer = externalDataLayer;
    }

    public void run(ProgressMonitor pm, Boundary boundary, boolean downloadOsm, boolean downloadOds) throws ExecutionException, InterruptedException {
        this.boundary = boundary;
        this.downloadOsm = downloadOsm;
        this.downloadOds = downloadOds;
        pm.indeterminateSubTask(I18n.tr("Setup"));
        setup();
        prepare();
        if (cancelled || interrupted) {
            pm.finishTask();
            return;
        }
        pm.indeterminateSubTask(I18n.tr("Downloading"));
        download();
        if (cancelled || interrupted) {
            pm.finishTask();
            return;
        }
        pm.indeterminateSubTask(I18n.tr("Processing data"));
        try {
            build();
        } catch (BuildException e) {
            throw new ExecutionException(e);
        }
        
        Bounds bounds = boundary.getBounds();
        DataSource ds = new DataSource(bounds, "Import");
        OsmDataLayer osmDataLayer =externalDataLayer.getOsmDataLayer();
        osmDataLayer.data.dataSources.add(ds);
        computeBboxAndCenterScale(bounds);
        pm.finishTask();
//        module.activate();
    }

    /**
     * Setup the download jobs. One job for the Osm data and one for imported data.
     * Setup the download tasks. Maybe more than 1 per job. 
     */
    private void setup() {
        downloadTasks = new LinkedList<DownloadTask>();
        if (downloadOsm) {
            internalDownloadJob.setBoundary(boundary);
            downloadTasks.addAll(internalDownloadJob.getDownloadTasks());
        }
        if (downloadOds) {
            externalDownloadJob.setBoundary(boundary);
            downloadTasks.addAll(externalDownloadJob.getDownloadTasks());
        }
    }

    /**
     * Prepare the 
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void prepare() {
        executor = Executors.newFixedThreadPool(NTHREADS);
        List<Callable<Object>> tasks = new LinkedList<>();
        for (DownloadTask task : downloadTasks) {
            tasks.add(task.getPrepareCallable());
        }
        interrupted = false;
        List<Exception> exceptions = new LinkedList<>();
        List<Future<Object>> futures = new LinkedList<>();
        try {
            futures = executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            interrupted = true;
        }
        executor.shutdown();
        for (Future<Object> future : futures) {
            if (future.isCancelled()) {
                cancelled = true;
            }
        }
        for (DownloadTask task : downloadTasks) {
            if (task.failed()) {
                cancelled = true;
                if (task.getMessage() != null) {
                    JOptionPane.showMessageDialog(Main.parent, task.getMessage());
                }
            }
        }
        for (DownloadTask task : downloadTasks) {
            if (task.cancelled()) {
                cancelled = true;
                if (task.getMessage() != null) {
                    JOptionPane.showMessageDialog(Main.parent, task.getMessage());
                }
            }
        }
        if (cancelled) return;
        for (DownloadTask task : downloadTasks) {
            if (task.getMessage() != null) {
                JOptionPane.showMessageDialog(Main.parent, task.getMessage());
            }
        }
//        if (!exceptions.isEmpty()) {
//            StringBuilder msg = new StringBuilder();
//            msg.append(I18n.trn("An error occured while downloading the data:", 
//                "{0} errors occurred while downloading the data:", exceptions.size(), exceptions.size()));
//            for (Exception e: exceptions) {
//                msg.append("\n").append(e.getMessage());
//            }
//            throw new ExecutionException(msg.toString(), null);
//        }
    }

    private void download() throws ExecutionException, InterruptedException {
//        module.activate();
        List<Future<?>> futures = new ArrayList<Future<?>>(downloadTasks.size());

        executor = Executors.newFixedThreadPool(NTHREADS);
        for (DownloadTask task : downloadTasks) {
            Future<?> future = executor.submit(task.getDownloadCallable());
            futures.add(future);
        }
        executor.shutdown();
        boolean interrupted = false;
        List<Exception> exceptions = new LinkedList<Exception>();
        // Wait for all futures to finish
        for (Future<?> future : futures) {
            try {
                future.get();
                if (future.isCancelled()) {
                    executor.shutdownNow();
                    interrupted = true;                    
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                interrupted = true;
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
        for (DownloadTask task : downloadTasks) {
            if (task.failed()) {
                cancelled = true;
                if (task.getMessage() != null) {
                    JOptionPane.showMessageDialog(Main.parent, task.getMessage());
                }
            }
        }
        for (DownloadTask task : downloadTasks) {
            if (task.cancelled()) {
                cancelled = true;
                if (task.getMessage() != null) {
                    JOptionPane.showMessageDialog(Main.parent, task.getMessage());
                }
            }
        }
        if (cancelled) return;
        for (DownloadTask task : downloadTasks) {
            if (task.getMessage() != null) {
                JOptionPane.showMessageDialog(Main.parent, task.getMessage());
            }
        }
        if (!exceptions.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            msg.append(I18n.trn("An error occured while downloading the data:", 
                "{0} errors occurred while downloading the data:", exceptions.size(), exceptions.size()));
            for (Exception e: exceptions) {
                msg.append("\n").append(e.getMessage());
            }
            throw new ExecutionException(msg.toString(), null);
        }
    }
    
    private void build() throws BuildException {
        if (downloadOsm) {
           internalDownloadJob.build();
        }
        if (downloadOds) {
            externalDownloadJob.build();
        }
    }

    protected void computeBboxAndCenterScale(Bounds bounds) {
        BoundingXYVisitor v = new BoundingXYVisitor();
        if (bounds != null) {
            v.visit(bounds);
            Main.map.mapView.recalculateCenterScale(v);
        }
    }

    public void cancel() {
        cancelled = true;
        if (executor != null) {
            executor.shutdownNow();
        }
    }
}
