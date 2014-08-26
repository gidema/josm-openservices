package org.openstreetmap.josm.plugins.ods.io;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.DataSource;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.external.GeotoolsDownloadJob;
import org.openstreetmap.josm.plugins.ods.entities.internal.InternalDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.internal.OsmDownloadJob;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
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
    private Boundary boundary;
    
//    private ProgressMonitor pm;
    
    private OsmDownloadJob osmDownloadJob;
    private GeotoolsDownloadJob geotoolsDownloadJob;
    private InternalDataLayer internalDataLayer;
    private ExternalDataLayer externalDataLayer;
    
    private ExecutorService executor;

    private Status status = new Status();

    @Inject
    public OdsDownloader(OsmDownloadJob osmDownloadJob, 
            GeotoolsDownloadJob geotoolsDownloadJob,
            ExternalDataLayer externalDataLayer,
            InternalDataLayer internalDataLayer
            ) {
        super();
//        this.module = module;
        this.osmDownloadJob = osmDownloadJob;
        this.geotoolsDownloadJob = geotoolsDownloadJob;
        this.internalDataLayer = internalDataLayer;
        this.externalDataLayer = externalDataLayer;
    }

    public void run(ProgressMonitor pm, Boundary boundary, boolean downloadOsm, boolean downloadOds) throws ExecutionException, InterruptedException {
        status.clear();
        this.boundary = boundary;
        this.downloadOsm = downloadOsm;
        this.downloadOds = downloadOds;
        pm.indeterminateSubTask(I18n.tr("Setup"));
        setup();
        task("prepare");
        if (!status.isSucces()) {
            pm.finishTask();
            return;
        }
        pm.indeterminateSubTask(I18n.tr("Downloading"));
        task("download");
        if (!status.isSucces()) {
            pm.finishTask();
            return;
        }
        pm.indeterminateSubTask(I18n.tr("Processing data"));
        task("process");
        if (!status.isSucces()) {
            pm.finishTask();
            return;
        }
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
        status.clear();
        downloaders = new LinkedList<Downloader>();
        if (downloadOsm) {
//            osmDownloadJob.setBoundary(boundary);
            for (Downloader downloader : osmDownloadJob.getDownloaders()) {
                downloader.setBoundary(boundary);
                downloaders.add(downloader);
            }
        }
        geotoolsDownloadJob.setBoundary(boundary);
        for (Downloader downloader : geotoolsDownloadJob.getDownloaders()) {
            downloader.setBoundary(boundary);
            downloaders.add(downloader);
        }
    }

//    /**
//     * @throws ExecutionException
//     * @throws InterruptedException
//     */
//    private void prepare() {
//        task("prepare");
//        executor = Executors.newFixedThreadPool(NTHREADS);
//        status.clear();
//        List<Runnable> tasks = new LinkedList<>();
//        for (final Downloader downloader : downloaders) {
//            Runnable task = new Runnable() {
//                @Override
//                public void run() {
//                    downloader.prepare();
//                }
//            };
//            tasks.add(task);
//            executor.execute(task);
//        }
//        executor.shutdown();
//        try {
//            executor.awaitTermination(1, TimeUnit.MINUTES);
//        }
//        catch (InterruptedException e) {
//            executor.shutdownNow();
//            for (Downloader downloader : downloaders) {
//                downloader.getStatus().setCancelled(true);
//            }
//        }
//        for (Downloader downloader : downloaders) {
//            Status status = downloader.getStatus();
//            if (!status.isSucces()) {
//                this.status = status;
//            }
//        }
//        if (!status.isSucces()) return;
//    }
//
//    private void download() throws ExecutionException, InterruptedException {
//        executor = Executors.newFixedThreadPool(NTHREADS);
//        List<Runnable> tasks = new LinkedList<>();
//        for (final Downloader downloader : downloaders) {
//            Runnable task = new Runnable() {
//                @Override
//                public void run() {
//                    downloader.download();
//                }
//            };
//            tasks.add(task);
//            executor.execute(task);
//        }
//        executor.shutdown();
//        try {
//            executor.awaitTermination(1, TimeUnit.MINUTES);
//        }
//        catch (InterruptedException e) {
//            executor.shutdownNow();
//            for (Downloader downloader : downloaders) {
//                downloader.getStatus().setCancelled(true);
//            }
//        }
//        for (Downloader downloader : downloaders) {
//            Status status = downloader.getStatus();
//            if (status.isFailed()) {
//                cancelled = true;
//                if (status.getMessage() != null) {
//                    JOptionPane.showMessageDialog(Main.parent, status.getMessage());
//                }
//            }
//        }
//        for (Downloader downloader : downloaders) {
//            Status status = downloader.getStatus();
//            if (status.isCancelled()) {
//                cancelled = true;
//                if (status.getMessage() != null) {
//                    JOptionPane.showMessageDialog(Main.parent, status.getMessage());
//                }
//            }
//        }
//        if (cancelled) return;
//        for (Downloader downloader : downloaders) {
//            Status status = downloader.getStatus();
//            if (status.getMessage() != null) {
//                JOptionPane.showMessageDialog(Main.parent, status.getMessage());
//            }
//        }
//    }
//    
//    private void process() throws ExecutionException, InterruptedException {
//        executor = Executors.newFixedThreadPool(NTHREADS);
//        interrupted = false;
//        List<Runnable> tasks = new LinkedList<>();
//        for (final Downloader downloader : downloaders) {
//            Runnable task = new Runnable() {
//                @Override
//                public void run() {
//                    downloader.process();
//                }
//            };
//            tasks.add(task);
//            executor.execute(task);
//        }
//        executor.shutdown();
//        try {
//            executor.awaitTermination(1, TimeUnit.MINUTES);
//        }
//        catch (InterruptedException e) {
//            executor.shutdownNow();
//            for (Downloader downloader : downloaders) {
//                downloader.getStatus().setCancelled(true);
//            }
//        }
//        for (Downloader downloader : downloaders) {
//            Status status = downloader.getStatus();
//            if (status.isFailed()) {
//                cancelled = true;
//                if (status.getMessage() != null) {
//                    JOptionPane.showMessageDialog(Main.parent, status.getMessage());
//                }
//            }
//        }
//        for (Downloader downloader : downloaders) {
//            Status status = downloader.getStatus();
//            if (status.isCancelled()) {
//                cancelled = true;
//                if (status.getMessage() != null) {
//                    JOptionPane.showMessageDialog(Main.parent, status.getMessage());
//                }
//            }
//        }
//        if (cancelled) return;
//        for (Downloader downloader : downloaders) {
//            Status status = downloader.getStatus();
//            if (status.getMessage() != null) {
//                JOptionPane.showMessageDialog(Main.parent, status.getMessage());
//            }
//        }
//    }
    
    private void task(final String methodName) {
        executor = Executors.newFixedThreadPool(NTHREADS);
        System.out.println("Executor:" + Thread.currentThread());
        System.out.println("Executor:" + Thread.currentThread().getThreadGroup());
        status.clear();
        List<Runnable> tasks = new LinkedList<>();
        for (final Downloader downloader : downloaders) {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    try {
                        Method method = Downloader.class.getDeclaredMethod(methodName);
                        method.invoke(downloader);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException("Unable to run method '" + methodName + "'. " +
                                "This must be a programming error");
                    } catch (SecurityException e) {
                        throw new RuntimeException("Unable to run method '" + methodName + "'. " +
                                "This must be a programming error");
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Unable to run method '" + methodName + "'. " +
                                "This must be a programming error");
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Unable to run method '" + methodName + "'. " +
                                "This must be a programming error");
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException("Unable to run method '" + methodName + "'. " +
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
    
    private void build() throws BuildException {
        if (downloadOsm) {
           osmDownloadJob.build();
        }
        if (downloadOds) {
            geotoolsDownloadJob.build();
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
}
