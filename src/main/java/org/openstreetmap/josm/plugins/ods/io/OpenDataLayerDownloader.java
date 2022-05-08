package org.openstreetmap.josm.plugins.ods.io;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.context.ContextJobList;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.context.OdsContextJob;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractGeoEntityStore;
import org.openstreetmap.josm.plugins.ods.wfs.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.wfs.WfsFeatureSources;

// TODO decide upon and document Class lifecycle
public class OpenDataLayerDownloader implements LayerDownloader {
    private static final int NTHREADS = 10;

    OdsContext context;
    private final List<FeatureDownloader> downloaders = new LinkedList<>();
    final List<FutureTask<TaskStatus>> prepareTasks = new LinkedList<>();
    final List<FutureTask<TaskStatus>> downloadTasks = new LinkedList<>();
    final List<FutureTask<TaskStatus>> processTasks = new LinkedList<>();
    private Status status = new Status();
    
    public OpenDataLayerDownloader() {
    }

    @Override
    public void setup(OdsContext context) {
        this.context = context;
        this.downloaders.clear();
        this.prepareTasks.clear();
        this.downloadTasks.clear();
        this.processTasks.clear();
        String operationMode = context.getParameter(ODS.OPERATION_MODE);
        WfsFeatureSources completeFeatureSources = context.getComponent(WfsFeatureSources.class, "Import");
        WfsFeatureSources modifiedFeatureSources = context.getComponent(WfsFeatureSources.class, "Update");
        modifiedFeatureSources.forEach(fs -> {
            FeatureDownloader downloader = new FeatureDownloader(context, fs);
            downloaders.add(downloader);
            prepareTasks.add(downloader.getPrepareTask());
            downloadTasks.add(downloader.getDownloadTask());
            processTasks.add(downloader.getProcessTask());
        });
        if (operationMode.equals("Import")) {
            completeFeatureSources.forEach(fs -> {
                FeatureDownloader downloader = new FeatureDownloader(context, fs);
                downloaders.add(downloader);
                prepareTasks.add(downloader.getPrepareTask());
                downloadTasks.add(downloader.getDownloadTask());
                processTasks.add(downloader.getProcessTask());
            });
        }
        this.context = context;
    }


    @Override
    public FutureTask<TaskStatus> getPrepareTask() {
        return new FutureTask<>(new PrepareTask());
    }

    
    @Override
    public FutureTask<TaskStatus> getDownloadTask() {
        return new FutureTask<>(new DownloadTask());
    }
    
    @Override
    public FutureTask<TaskStatus> getProcessTask() {
        return new FutureTask<>(new ProcessTask());
    }

    private class PrepareTask implements Callable<TaskStatus> {
        public PrepareTask() {
            // 
        }

        @Override
        public TaskStatus call() {
            return runTasks(prepareTasks);
        }
    }

    private class DownloadTask implements Callable<TaskStatus> {
        public DownloadTask() {
            // 
        }

        @Override
        public TaskStatus call() {
            runTasks(downloadTasks);
            updateStoreBoundaries();
            return new TaskStatus();
        }
        
        /**
         * Entity stores must keep track of the boundaries of the downloaded areas.
         * This method updates the boundaries for all entity stores.
         */
        private void updateStoreBoundaries() {
            List<AbstractGeoEntityStore<?>> entityStores = context.getComponents(AbstractGeoEntityStore.class);
            DownloadRequest request = context.getComponent(DownloadRequest.class);
            entityStores.forEach(store -> store.extendBoundary(request.getBoundary().getMultiPolygon()));
        }
    }
    

    private class ProcessTask implements Callable<TaskStatus> {
        public ProcessTask() {
            //
        }

        @Override
        public TaskStatus call() {
            runTasks(processTasks);
    
            DownloadRequest request = context.getComponent(DownloadRequest.class);
            OsmDataLayer osmDataLayer = context.getComponent(OdLayerManager.class).getOsmDataLayer();
            request.getBoundary().getBounds().forEach(bounds -> {
                DataSource ds = new DataSource(bounds, "Import");
                osmDataLayer.getDataSet().addDataSource(ds);
            });
            ContextJobList jobs = context.getComponent(ContextJobList.class, "postDownloadJobs");
            new ModifiedAreasFactory().run(context);
            for (OdsContextJob job : jobs) {
                // TOOD add timing and show results in process monitor
                job.run(context);
            }
            return new TaskStatus();
        }
    }

    static TaskStatus runTasks(List<FutureTask<TaskStatus>> tasks) {
        ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
        tasks.forEach(executor::execute);
        try {
            while (true) {
                boolean allDone = true;
                for (FutureTask<TaskStatus> task : tasks) {
                    allDone &= task.isDone();
                }
                if (allDone) {
                    executor.shutdown();
                    List<TaskStatus> taskStatuses = new ArrayList<>(tasks.size());
                    for (FutureTask<TaskStatus> task : tasks) {
                        TaskStatus taskStatus;
                        try {
                            taskStatus = task.get();
                        }
                        catch (ExecutionException e) {
                            taskStatus = new TaskStatus(null, null, e);
                        }
                        taskStatuses.add(taskStatus);
                    }
                    return new TaskStatus(taskStatuses);
                }
            }
        }
        catch (InterruptedException e) {
            executor.shutdownNow();
            tasks.forEach(task -> task.cancel(true));
            return new TaskStatus(true);
        }
    }

    @Override
    public void cancel() {
//        if (executor != null) {
//            executor.shutdownNow();
//        }
        this.status.setCancelled(true);
    }
}
