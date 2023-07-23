package org.openstreetmap.josm.plugins.ods.wfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.OdEntityFactories;
import org.openstreetmap.josm.plugins.ods.entities.OdEntityFactory;
import org.openstreetmap.josm.plugins.ods.io.DataCutOffException;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.io.TaskStatus;
import org.openstreetmap.josm.plugins.ods.wfs.query.BboxWfsFilter;
import org.openstreetmap.josm.plugins.ods.wfs.query.OdsQueryFilter;
import org.openstreetmap.josm.plugins.ods.wfs.query.WfsQuery;
import org.openstreetmap.josm.plugins.ods.wfs.query.WfsRequest;
import org.openstreetmap.josm.tools.I18n;

public class FeatureDownloader {
    final WfsFeatureSource featureSource;
    final OdsContext context;
    WfsFeatureCollection downloadedFeatures = new WfsFeatureCollectionImpl();
    private final ProcessTask processTask;

    public FeatureDownloader(OdsContext context, WfsFeatureSource featureSource) {
        super();
        this.context = context;
        this.featureSource = featureSource;
        this.processTask = new ProcessTask();
    }

    @SuppressWarnings("static-method")
    public FutureTask<TaskStatus> getPrepareTask() {
        return null;
    }
    
    public Collection<FutureTask<TaskStatus>> getFetchTasks() {
        // Transform the request to the right coordinate system
        // TODO Technical debt: Wouldn't it be better to transform the bounding box rather than the request?
        final DownloadRequest request = transformRequest(context.getComponent(DownloadRequest.class));
        WfsHost host = featureSource.getHost();
        String url = host.getUrl().toString();
        Integer pageSize = featureSource.getPageSize();
        QName featureName = featureSource.getFeatureType();
        QName geometryProperty = featureSource.getGeometryProperty();
        List<FutureTask<TaskStatus>> tasks = new ArrayList<>(request.getBoundary().getBounds().size());
        request.getBoundary().getBounds().forEach( bounds -> {
            OdsQueryFilter filter = new BboxWfsFilter(geometryProperty, bounds, featureSource.getSrid());
            WfsQuery query = new WfsQuery(featureName, filter, featureSource.getSrid(), null);
            FutureTask<TaskStatus> task = new FutureTask<>(new FetchTask(url, query, pageSize));
            tasks.add(task);
        });
        return tasks;
    }
    
    public FutureTask<TaskStatus> getProcessTask() {
        return new FutureTask<>(processTask);
    }

    private DownloadRequest transformRequest(DownloadRequest request) {
        if (featureSource.getSrid() == CRSUtil.OSM_SRID) return request;
        CRSUtil crsUtil = context.getComponent(CRSUtil.class);
        return request.transform(crsUtil, featureSource.getSrid());
    }

    class FetchTask implements Callable<TaskStatus> {
        private final String url;
        private final WfsQuery query;
        private final Integer pageSize;
        

        public FetchTask(String url, WfsQuery query, Integer pageSize) {
            super();
            this.url = url;
            this.query = query;
            this.pageSize = pageSize;
        }

        @Override
        public TaskStatus call() {
            WfsRequest wfsRequest = new WfsRequest(url, query, 1, pageSize, featureSource.getSortBy());
            WfsFeatureReader reader;
            if (featureSource.getPageSize() > 0) {
                reader = new PagingWfsFeatureReader(featureSource, wfsRequest, context);
            }
            else {
                reader = new SimpleWfsFeatureReader(featureSource, wfsRequest, context);
            }
            TaskStatus taskStatus;
            try {
                downloadedFeatures = reader.read();
                taskStatus = new TaskStatus();
            } catch (DataCutOffException e) {
                downloadedFeatures.clear();
                String error = I18n.tr(
                        "To many {0} objects. Please choose a smaller download area.", featureSource.getFeatureType());
                taskStatus = new TaskStatus(null, error, null);
            }
            catch (IOException e) {
                if (featureSource.isRequired()) {
                    String error = I18n.tr(
                        "IO Exception occurred while trying to download feature of type {0}. Because this feature type is required, the download will be cancelled.\n" + 
                        "The error message was: {1}.", featureSource.getFeatureType(), e.getMessage());
                    taskStatus = new TaskStatus(null, error, e);
                }
                else {
                    String warning = I18n.tr(
                            "IO Exception occurred while trying to download feature of type {0}. Because this feature type is not required, this feature type will be ignored.\n" + 
                            "The error message was: {1}.", featureSource.getFeatureType(), e.getMessage());
                        taskStatus = new TaskStatus(warning, null, e);
                }
            }
           if (taskStatus.hasErrors() || taskStatus.hasExceptions()) {
                Thread.currentThread().interrupt();
            }
           return taskStatus;
        }
    }
    
    class ProcessTask implements Callable<TaskStatus> {
        @Override
        public TaskStatus call() {
            DownloadResponse response = context.getComponent(DownloadResponse.class);
            List<OdEntityFactory> entityFactories = context.getComponent(OdEntityFactories.class).getFactories(featureSource.getFeatureType());
            if (entityFactories.isEmpty()) {
                String warning = I18n.tr("No entity factories were found for feature type: {0}. Features of this type will not be imported.", featureSource.getFeatureType());
                return new TaskStatus(warning, null, null);
            }
            entityFactories.forEach(f -> this.createEntities(f, response));
            return new TaskStatus();
        }

        private void createEntities(OdEntityFactory factory, DownloadResponse response) {
            downloadedFeatures.forEach(feature -> factory.process(feature, response));
        }
    }

}