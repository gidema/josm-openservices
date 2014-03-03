package org.openstreetmap.josm.plugins.ods;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.DataSource;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.plugins.ods.entities.external.DownloadExternalLayerTask;
import org.openstreetmap.josm.plugins.ods.entities.internal.DownloadInternalLayerTask;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.tools.I18n;

public class OdsDownloader {
    private static final int NTHREADS = 10;

    private OdsWorkingSet workingSet;
    
    private DownloadInternalLayerTask internalDownloadTask;
    private DownloadExternalLayerTask externalDownloadTask;

    private boolean downloadOsm;
    private boolean downloadOds;
    
    private List<DownloadTask> downloadTasks;
    private Boundary boundary;
    
    private ProgressMonitor pm;
    
    boolean cancelled = false;
    boolean interrupted = false;
    
    private ExecutorService executor;

    protected OdsDownloader(Boundary boundary, ProgressMonitor progressMonitor) {
        super();
        this.workingSet = ODS.getModule().getWorkingSet();
        this.boundary = boundary;
        this.pm = progressMonitor;
    }

    public void run(boolean downloadOsm, boolean downloadOds) throws ExecutionException, InterruptedException {
        this.downloadOsm = downloadOsm;
        this.downloadOds = downloadOds;
        setup();
        pm.indeterminateSubTask(I18n.tr("Preparing"));
        subTask("prepare");
        if (cancelled || interrupted) {
            pm.finishTask();
            return;
        }
        pm.indeterminateSubTask(I18n.tr("Downloading"));
        subTask("download");
        if (cancelled || interrupted) {
            pm.finishTask();
            return;
        }
        pm.indeterminateSubTask(I18n.tr("Processing data"));
        subTask("process");
        if (cancelled || interrupted) {
            pm.finishTask();
            return;
        }
        
        Bounds bounds = boundary.getBounds();
        DataSource ds = new DataSource(bounds, "Import");
        OsmDataLayer exernalDataLayer = workingSet.getExternalDataLayer().getOsmDataLayer();
        exernalDataLayer.data.dataSources.add(ds);
//        pm.finishTask();
        computeBboxAndCenterScale(bounds);
        pm.finishTask();
        workingSet.activate();
//        if (downloadOds) {
//            downloadTasks.addAll(externalDownloadJob.getDownloadTasks());
//        }
//        else {
//            downloadTasks.addAll(internalDownloadJob.getDownloadTasks());            
//        }
    }

    /**
     * Setup the download task. One job for the Osm layer and one for imported data layer.
     * Setup the download tasks. Maybe more than 1 per job. 
     */
    private void setup() {
        downloadTasks = new ArrayList<DownloadTask>(2);
        if (downloadOsm) {
            internalDownloadTask = new DownloadInternalLayerTask(boundary);
            downloadTasks.add(internalDownloadTask);
        }
        if (downloadOds) {
            externalDownloadTask = new DownloadExternalLayerTask(boundary);
            downloadTasks.add(externalDownloadTask);
        }
    }

    private void subTask(String subTask) throws ExecutionException, InterruptedException {
        workingSet.activate();
        List<Future<?>> futures = new ArrayList<Future<?>>(downloadTasks.size());

        executor = Executors.newFixedThreadPool(NTHREADS);
        for (DownloadTask task : downloadTasks) {
            Callable<Object> callable = task.stage(subTask);
            if (callable != null) {
                Future<?> future = executor.submit(callable);
                futures.add(future);
            }
        }
        executor.shutdown();
        interrupted = false;
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

//    /**
//     * Prepare the 
//     * @throws ExecutionException
//     * @throws InterruptedException
//     */
//    private void prepare() {
//        executor = Executors.newFixedThreadPool(NTHREADS);
//        List<Callable<Object>> tasks = new LinkedList<>();
//        for (DownloadLayerTask task : downloadTasks) {
//            tasks.add(task.prepare());
//        }
//        interrupted = false;
////        List<Exception> exceptions = new LinkedList<>();
//        List<Future<Object>> futures = new LinkedList<>();
//        try {
//            futures = executor.invokeAll(tasks);
//        } catch (InterruptedException e) {
//            interrupted = true;
//        }
//        executor.shutdown();
//        for (Future<Object> future : futures) {
//            if (future.isCancelled()) {
//                cancelled = true;
//            }
//        }
//        for (DownloadLayerTask task : downloadTasks) {
//            if (task.failed()) {
//                cancelled = true;
//                if (task.getMessage() != null) {
//                    JOptionPane.showMessageDialog(Main.parent, task.getMessage());
//                }
//            }
//        }
//        for (DownloadLayerTask task : downloadTasks) {
//            if (task.cancelled()) {
//                cancelled = true;
//                if (task.getMessage() != null) {
//                    JOptionPane.showMessageDialog(Main.parent, task.getMessage());
//                }
//            }
//        }
//        if (cancelled) return;
//        for (DownloadLayerTask task : downloadTasks) {
//            if (task.getMessage() != null) {
//                JOptionPane.showMessageDialog(Main.parent, task.getMessage());
//            }
//        }
////        if (!exceptions.isEmpty()) {
////            StringBuilder msg = new StringBuilder();
////            msg.append(I18n.trn("An error occured while downloading the data:", 
////                "{0} errors occurred while downloading the data:", exceptions.size(), exceptions.size()));
////            for (Exception e: exceptions) {
////                msg.append("\n").append(e.getMessage());
////            }
////            throw new ExecutionException(msg.toString(), null);
////        }
//    }
//
//    private void download() throws ExecutionException, InterruptedException {
//        workingSet.activate();
//        List<Future<?>> futures = new ArrayList<Future<?>>(downloadTasks.size());
//
//        executor = Executors.newFixedThreadPool(NTHREADS);
//        for (DownloadLayerTask task : downloadTasks) {
//            Future<?> future = executor.submit(task.download());
//            futures.add(future);
//        }
//        executor.shutdown();
//        interrupted = false;
//        List<Exception> exceptions = new LinkedList<Exception>();
//        // Wait for all futures to finish
//        for (Future<?> future : futures) {
//            try {
//                future.get();
//                if (future.isCancelled()) {
//                    executor.shutdownNow();
//                    interrupted = true;                    
//                }
//            } catch (InterruptedException e) {
//                executor.shutdownNow();
//                interrupted = true;
//            } catch (Exception e) {
//                exceptions.add(e);
//            }
//        }
//        for (DownloadLayerTask task : downloadTasks) {
//            if (task.failed()) {
//                cancelled = true;
//                if (task.getMessage() != null) {
//                    JOptionPane.showMessageDialog(Main.parent, task.getMessage());
//                }
//            }
//        }
//        for (DownloadLayerTask task : downloadTasks) {
//            if (task.cancelled()) {
//                cancelled = true;
//                if (task.getMessage() != null) {
//                    JOptionPane.showMessageDialog(Main.parent, task.getMessage());
//                }
//            }
//        }
//        if (cancelled) return;
//        for (DownloadLayerTask task : downloadTasks) {
//            if (task.getMessage() != null) {
//                JOptionPane.showMessageDialog(Main.parent, task.getMessage());
//            }
//        }
//        if (!exceptions.isEmpty()) {
//            StringBuilder msg = new StringBuilder();
//            msg.append(I18n.trn("An error occured while downloading the data:", 
//                "{0} errors occurred while downloading the data:", exceptions.size(), exceptions.size()));
//            for (Exception e: exceptions) {
//                msg.append("\n").append(e.getMessage());
//            }
//            throw new ExecutionException(msg.toString(), null);
//        }
//    }
//    
//    private void process()  throws ExecutionException, InterruptedException {
//        workingSet.activate();
//        List<Future<?>> futures = new ArrayList<Future<?>>(downloadTasks.size());
//
//        executor = Executors.newFixedThreadPool(NTHREADS);
//        for (DownloadLayerTask task : downloadTasks) {
//            Future<?> future = executor.submit(task.process());
//            futures.add(future);
//        }
//        executor.shutdown();
//        interrupted = false;
//        List<Exception> exceptions = new LinkedList<Exception>();
//        // Wait for all futures to finish
//        for (Future<?> future : futures) {
//            try {
//                future.get();
//                if (future.isCancelled()) {
//                    executor.shutdownNow();
//                    interrupted = true;                    
//                }
//            } catch (InterruptedException e) {
//                executor.shutdownNow();
//                interrupted = true;
//            } catch (Exception e) {
//                exceptions.add(e);
//            }
//        }
//        for (DownloadLayerTask task : downloadTasks) {
//            if (task.failed()) {
//                cancelled = true;
//                if (task.getMessage() != null) {
//                    JOptionPane.showMessageDialog(Main.parent, task.getMessage());
//                }
//            }
//        }
//        for (DownloadLayerTask task : downloadTasks) {
//            if (task.cancelled()) {
//                cancelled = true;
//                if (task.getMessage() != null) {
//                    JOptionPane.showMessageDialog(Main.parent, task.getMessage());
//                }
//            }
//        }
//        if (cancelled) return;
//        for (DownloadLayerTask task : downloadTasks) {
//            if (task.getMessage() != null) {
//                JOptionPane.showMessageDialog(Main.parent, task.getMessage());
//            }
//        }
//        if (!exceptions.isEmpty()) {
//            StringBuilder msg = new StringBuilder();
//            msg.append(I18n.trn("An error occured while downloading the data:", 
//                "{0} errors occurred while downloading the data:", exceptions.size(), exceptions.size()));
//            for (Exception e: exceptions) {
//                msg.append("\n").append(e.getMessage());
//            }
//            throw new ExecutionException(msg.toString(), null);
//        }
//        
//    }
    
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
