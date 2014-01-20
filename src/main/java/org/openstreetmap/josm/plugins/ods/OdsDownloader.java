package org.openstreetmap.josm.plugins.ods;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.DataSource;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDownloadJob;
import org.openstreetmap.josm.plugins.ods.entities.internal.InternalDownloadJob;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.tools.I18n;

public class OdsDownloader {
    private static final int NTHREADS = 10;

    private OdsWorkingSet workingSet;
    
    private InternalDownloadJob internalDownloadJob;
    private ExternalDownloadJob externalDownloadJob;
    
    private List<DownloadTask> downloadTasks;
    private Boundary boundary;
    
    private ProgressMonitor pm;
    
    boolean cancelled = false;
    boolean interrupted = false;

    protected OdsDownloader(Boundary boundary, ProgressMonitor progressMonitor) {
        super();
        this.workingSet = ODS.getModule().getWorkingSet();
        this.boundary = boundary;
        this.pm = progressMonitor;
    }

    public void run() throws ExecutionException, InterruptedException {
        pm.indeterminateSubTask(I18n.tr("Setup"));
        setup();
        prepare();
        if (cancelled || interrupted) return;
        pm.indeterminateSubTask(I18n.tr("Downloading"));
        download();
        if (cancelled || interrupted) return;
        pm.indeterminateSubTask(I18n.tr("Processing data"));
        try {
            build();
        } catch (BuildException e) {
            throw new ExecutionException(e);
        }
        
        Bounds bounds = boundary.getBounds();
        DataSource ds = new DataSource(bounds, "Import");
        OsmDataLayer exernalDataLayer = workingSet.getExternalDataLayer().getOsmDataLayer();
        exernalDataLayer.data.dataSources.add(ds);
//        pm.finishTask();
        computeBboxAndCenterScale(bounds);
        pm.finishTask();
        workingSet.activate();
        Main.map.mapView.setActiveLayer(exernalDataLayer);
    }

    /**
     * Setup the download jobs. One job for the Osm data and one for imported data.
     * Setup the download tasks. Maybe more than 1 per job. 
     */
    private void setup() {
        internalDownloadJob = new InternalDownloadJob(boundary);
        internalDownloadJob.setup();
        externalDownloadJob = new ExternalDownloadJob(boundary);
        externalDownloadJob.setup();
        downloadTasks = new LinkedList<DownloadTask>();
        downloadTasks.addAll(internalDownloadJob.getDownloadTasks());
        downloadTasks.addAll(externalDownloadJob.getDownloadTasks());
    }

    /**
     * Prepare the 
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void prepare() {
        ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
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
        for (Future<Object> future : futures) {
            if (future.isCancelled()) {
                cancelled = true;
            }
        }
        boolean ready = false;
//            while (!ready) {
//                ready = true;
//                for (Future<?> future : futures) {
//                    if (future.isCancelled()) {
//                        cancelled = true;
//                    }
//                    if (!future.isDone()) {
//                        ready = false;
//                    }
//                }
//                try {
//                    future.get();
//                } catch (InterruptedException e) {
//                    interrupted = true;
//                } catch (Exception e) {
//                    exceptions.add(e);
//                }
//            }
//            if (interrupted) {
//                throw new InterruptedException();
//            }
//            if (!exceptions.isEmpty()) {
//                StringBuilder sb = new StringBuilder();
//                sb.append(I18n.trn("An error occurred while preparing the download jobs:",
//                        "{1} errors occurred while preparing the download jobs:", exceptions.size(), exceptions.size()));
//                for (Exception e : exceptions) {
//                    sb.append("\n").append(e.getMessage());
//                }
//                throw new ExecutionException(sb.toString(), null);
//            }
    }

    private void download() throws ExecutionException, InterruptedException {
        workingSet.activate();
        List<Future<?>> futures = new ArrayList<Future<?>>(downloadTasks.size());

        ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
        for (DownloadTask task : downloadTasks) {
            Future<?> future = executor.submit(task.getDownloadCallable());
            futures.add(future);
        }
        // TODO isn't this a bit early to shutdown the executor?
        executor.shutdown();
        boolean interrupted = false;
        List<Exception> exceptions = new LinkedList<Exception>();
        // Wait for all futures to finish
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                interrupted = true;
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
        if (interrupted) {
            throw new InterruptedException();
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
        internalDownloadJob.build();
        externalDownloadJob.build();
    }

    @Deprecated
    private static String getOverpassUrl(String query, Bounds bounds) {
        String host = "http://overpass-api.de/api";
        String bbox = String.format(Locale.ENGLISH, "%f,%f,%f,%f", bounds
                .getMin().getY(), bounds.getMin().getX(), bounds.getMax()
                .getY(), bounds.getMax().getX());
        String q = query.replaceAll("\\$bbox", bbox);
        q = q.replaceAll("\\{\\{bbox\\}\\}", bbox);
        q = q.replace(";$", "");
        return String.format("%s/interpreter?data=%s;out meta;", host, q);
    }

    protected void computeBboxAndCenterScale(Bounds bounds) {
        BoundingXYVisitor v = new BoundingXYVisitor();
        if (bounds != null) {
            v.visit(bounds);
            Main.map.mapView.recalculateCenterScale(v);
        }
    }
}
