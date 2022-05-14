package org.openstreetmap.josm.plugins.ods.wfs;

import java.io.IOException;
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
import org.openstreetmap.josm.plugins.ods.opengis.fes.FesFilter;
import org.openstreetmap.josm.plugins.ods.opengis.fes.FilterPredicate;
import org.openstreetmap.josm.plugins.ods.opengis.fes.IntersectsPredicate;
import org.openstreetmap.josm.plugins.ods.wfs.query.BboxWfsFilter;
import org.openstreetmap.josm.plugins.ods.wfs.query.OdsQueryFilter;
import org.openstreetmap.josm.plugins.ods.wfs.query.WfsQuery;
import org.openstreetmap.josm.plugins.ods.wfs.query.WfsRequest;
import org.openstreetmap.josm.tools.I18n;

public class FeatureDownloader {
    final WfsFeatureSource featureSource;
    final OdsContext context;
    WfsFeatureCollection downloadedFeatures = new WfsFeatureCollectionImpl();
    private final FetchTask fetchTask;
    private final ProcessTask processTask;

    public FeatureDownloader(OdsContext context, WfsFeatureSource featureSource) {
        super();
        this.context = context;
        this.featureSource = featureSource;
        this.fetchTask = new FetchTask();
        this.processTask = new ProcessTask();
    }

    @SuppressWarnings("static-method")
    public FutureTask<TaskStatus> getPrepareTask() {
        return null;
    }
    
    public FutureTask<TaskStatus> getFetchTask() {
        return new FutureTask<>(fetchTask);
    }
    
    public FutureTask<TaskStatus> getProcessTask() {
        return new FutureTask<>(processTask);
    }

    class FetchTask implements Callable<TaskStatus> {
        @Override
        public TaskStatus call() {
            DownloadRequest request = context.getComponent(DownloadRequest.class);
            // Transform the request to the right coordinate system
            request = transformRequest(request);
            WfsHost host = featureSource.getHost();
            String url = host.getUrl().toString();
            Integer pageSize = featureSource.getPageSize();
            QName featureName = featureSource.getFeatureType();
            QName geometryProperty = featureSource.getGeometryProperty();
            OdsQueryFilter filter;
            if (host.isFesFilterCapable()) {
                FilterPredicate intersects = new IntersectsPredicate(geometryProperty, request.getBoundary().getMultiPolygon());
                filter = new FesFilter(intersects);
            }
            else {
                if (request.getBoundary().getBounds().size() == 1) {
                    filter = new BboxWfsFilter(request.getBoundary(), featureSource.getSrid());
                }
                else {
                    throw new UnsupportedOperationException();
                }
            }
            WfsQuery query = new WfsQuery(featureName, filter, featureSource.getSrid(), null);
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

        private DownloadRequest transformRequest(DownloadRequest request) {
            if (featureSource.getSrid() == CRSUtil.OSM_SRID) return request;
            CRSUtil crsUtil = context.getComponent(CRSUtil.class);
            return request.transform(crsUtil, featureSource.getSrid());
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
