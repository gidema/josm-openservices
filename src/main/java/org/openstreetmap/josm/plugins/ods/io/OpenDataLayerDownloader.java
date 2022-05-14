package org.openstreetmap.josm.plugins.ods.io;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.context.ContextJobList;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.context.OdsContextJob;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractGeoEntityStore;
import org.openstreetmap.josm.plugins.ods.wfs.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.wfs.WfsFeatureSources;

// TODO decide upon and document Class lifecycle
public class OpenDataLayerDownloader implements LayerDownloader {

    OdsContext context;
    private final List<FeatureDownloader> downloaders = new LinkedList<>();
    final List<FutureTask<TaskStatus>> prepareTasks = new LinkedList<>();
    final List<FutureTask<TaskStatus>> fetchTasks = new LinkedList<>();
    final List<FutureTask<TaskStatus>> processTasks = new LinkedList<>();
    private boolean cancelled = false;
    
    public OpenDataLayerDownloader() {
    }

    @Override
    public void setup(OdsContext context) {
        this.context = context;
        this.downloaders.clear();
        this.prepareTasks.clear();
        this.fetchTasks.clear();
        this.processTasks.clear();
        WfsFeatureSources completeFeatureSources = context.getComponent(WfsFeatureSources.class, "Import");
        WfsFeatureSources modifiedFeatureSources = context.getComponent(WfsFeatureSources.class, "Update");
        modifiedFeatureSources.forEach(fs -> {
            FeatureDownloader downloader = new FeatureDownloader(context, fs);
            downloaders.add(downloader);
            prepareTasks.add(downloader.getPrepareTask());
            fetchTasks.add(downloader.getFetchTask());
            processTasks.add(downloader.getProcessTask());
        });
        completeFeatureSources.forEach(fs -> {
            FeatureDownloader downloader = new FeatureDownloader(context, fs);
            downloaders.add(downloader);
            prepareTasks.add(downloader.getPrepareTask());
            fetchTasks.add(downloader.getFetchTask());
            processTasks.add(downloader.getProcessTask());
        });
        this.context = context;
    }


    @Override
    public FutureTask<TaskStatus> getPrepareTask() {
        return new FutureTask<>(new PrepareTask());
    }

    
    @Override
    public FutureTask<TaskStatus> getFetchTask() {
        return new FutureTask<>(new FetchTask());
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
            return Downloader.runTasks(prepareTasks);
        }
    }

    private class FetchTask implements Callable<TaskStatus> {
        public FetchTask() {
            // 
        }

        @Override
        public TaskStatus call() {
            TaskStatus status = Downloader.runTasks(fetchTasks);
            // TODO isn't this a process task?
            updateStoreBoundaries();
            return status;
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
            TaskStatus status = Downloader.runTasks(processTasks);
            if (status.hasErrors() || status.hasExceptions()) {
                return status;
            }
    
            // TODO Isn't this just another process task?
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
            return status;
        }
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }
}
