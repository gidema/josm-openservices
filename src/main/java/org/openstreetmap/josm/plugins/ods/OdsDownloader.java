package org.openstreetmap.josm.plugins.ods;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.DataSource;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.analysis.GlobalAnalyzer;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.GlobalBlockBuilder;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDownloadJob;
import org.openstreetmap.josm.plugins.ods.entities.internal.InternalDownloadJob;
import org.openstreetmap.josm.tools.I18n;

public class OdsDownloader {
    private static final int NTHREADS = 10;

    private final OdsWorkingSet workingSet;
    private InternalDownloadJob internalDownloadJob;
    private ExternalDownloadJob externalDownloadJob;
    
    private List<DownloadTask> downloadTasks;
    private Bounds bounds;

    private List<GlobalAnalyzer> analyzers = new LinkedList<>();

    protected OdsDownloader(OdsWorkingSet workingSet, Bounds bounds) {
        super();
        this.workingSet = workingSet;
        this.bounds = bounds;
        Double tolerance = 1e-7;
        analyzers.add(new GlobalBlockBuilder(tolerance));
    }

    public void run() throws ExecutionException, InterruptedException {
//        ProgressMonitor monitor = new PleaseWaitProgressMonitor(Main.map, I18n.tr("Downloading"));
//        monitor.beginTask("Setup");
        setup();
        prepare();
        download();
        System.out.println("Download finished");
        try {
            build();
        } catch (BuildException e) {
            throw new ExecutionException(e);
        }
        DataSource ds = new DataSource(bounds, "Import");
        OsmDataLayer exernalDataLayer = workingSet.getExternalDataLayer().getOsmDataLayer();
        exernalDataLayer.data.dataSources.add(ds);
//        monitor.finishTask();
        computeBboxAndCenterScale();
        workingSet.activate();
        Main.map.mapView.setActiveLayer(exernalDataLayer);
    }

    /**
     * Setup the download jobs. One job for the Osm data and one for imported data.
     * Setup the download tasks. Maybe more than 1 per job. 
     */
    private void setup() {
        internalDownloadJob = new InternalDownloadJob(workingSet, bounds);
        internalDownloadJob.setup();
        externalDownloadJob = new ExternalDownloadJob(workingSet, bounds);
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
    private void prepare() throws ExecutionException, InterruptedException {
        List<Future<?>> futures = new ArrayList<Future<?>>(downloadTasks.size());

        ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
        try {
            for (DownloadTask task : downloadTasks) {
                Future<?> future = executor.submit(task.getPrepareCallable());
                futures.add(future);
            }
            // Wait for all futures to finish
            boolean interrupted = false;
            List<Exception> exceptions = new LinkedList<Exception>();
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
                StringBuilder sb = new StringBuilder();
                sb.append(I18n.trn("An error occurred while preparing the download jobs:",
                        "{0} errors occurred while preparing the download jobs:", exceptions.size(), exceptions.size()));
                for (Exception e : exceptions) {
                    sb.append("\n").append(e.getMessage());
                }
                throw new ExecutionException(sb.toString(), null);
            }
        } 
        finally {
            executor.shutdown();
         }
    }

    private void download() throws ExecutionException, InterruptedException {
        workingSet.activate();
//        workingSet.activateOsmLayer();
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
                "{0} errors occurred while downloading the data:", exceptions.size()));
            for (Exception e: exceptions) {
                msg.append("\n").append(e.getMessage());
            }
            throw new ExecutionException(msg.toString(), null);
        }
        workingSet.getExternalDataLayer().getEntitySet().extendBoundary(bounds);
        // Retrieve the results
    }
    
    private void build() throws BuildException {
        internalDownloadJob.build();
        externalDownloadJob.build();
        for (GlobalAnalyzer analyzer: analyzers) {
            analyzer.analyze(workingSet);
        }
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

    protected void computeBboxAndCenterScale() {
        BoundingXYVisitor v = new BoundingXYVisitor();
        if (bounds != null) {
            v.visit(bounds);
            Main.map.mapView.recalculateCenterScale(v);
        }
    }
}
