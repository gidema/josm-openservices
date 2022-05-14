package org.openstreetmap.josm.plugins.ods.io;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.OsmApiException;
import org.openstreetmap.josm.io.OsmServerReader;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntityBuilders;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

public class OsmLayerDownloader implements LayerDownloader {
    DownloadSource downloadSource =  DownloadSource.OVERPASS;
    private Collection<OsmServerReader> osmServerReaders;
    OdsContext context;
    DownloadRequest request;

    DataSet dataSet;

    static enum DownloadSource {
        OSM,
        OVERPASS;
    }
    
    public OsmLayerDownloader() {
        super();
    }

    @Override
    public void setup(OdsContext context) {
        this.context = context;
        this.request =context.getComponent(DownloadRequest.class);
    }


    @Override
    public FutureTask<TaskStatus> getPrepareTask() {
        return null;
    }

    @Override
    public FutureTask<TaskStatus> getFetchTask() {
        String operationMode = context.getParameter(ODS.OPERATION_MODE);
        if (operationMode.equals("Update")) {
            return null;
        }
        return new FutureTask<>(new DownloadTask());
    }

    @Override
    public FutureTask<TaskStatus> getProcessTask() {
        return new FutureTask<>(new ProcessTask());
    }

    public class DownloadTask implements Callable<TaskStatus> { 
        @Override
        public TaskStatus call() {
            String operationMode = context.getParameter(ODS.OPERATION_MODE);
            dataSet = new DataSet();
            OsmHost host = getHost();
            try {
                // TODO We currently run the OSM requests sequentially.
                // For Overpass this doesn't matter as we use a single request.
                // If we use the OSM server, it would be nice if we would call parallel requests
                for (OsmServerReader osmServerReader : host.getServerReaders(request)) {
                    dataSet.mergeFrom(parseDataSet(osmServerReader));
                }
                return new TaskStatus();
            } catch (MalformedURLException e) {
                String error = I18n.tr("There is an error in the URL for the {0} server. The download has been cancelled.\n" +
                        "The error message was: {1}", host.getHostString(), e.getMessage());
                   return new TaskStatus(null, error, e);
            }
            catch (OsmApiException e) {
                switch (e.getResponseCode()) {
                case 400:
                    String error = I18n.tr("You tried to download too much Openstreetmap data. Please select a smaller download area.");
                    return new TaskStatus(null, error, e);
                case 404:
                    error = I18n.tr("No OSM server could be found at this location: {0}", 
                        host.getHostString().toString());
                    return new TaskStatus(null, error, e);
                default:
                    Logging.error(e);
                    if (e.getCause() instanceof UnknownHostException) {
                        error = I18n.tr("Could not connect to OSM server ({0}). Please check your Internet connection.",  host.getHostString());
                        return new TaskStatus(null, error, e.getCause());
                    }
                    return new TaskStatus(null, e.getMessage(), e);
                }
            }
            catch(OsmTransferException e) {
                Logging.error(e);
                String error = I18n.tr("An Osm transfer exception occurred: {0}.", e.getMessage());
                return new TaskStatus(null, error, e);
            }
        }

        private DataSet parseDataSet(OsmServerReader osmServerReader) throws OsmTransferException {
            return osmServerReader.parseOsm(NullProgressMonitor.INSTANCE);
        }

        private OsmHost getHost() {
            switch (downloadSource) {
            case OSM:
                return new PlainOsmHost();
            case OVERPASS:
                return new OverpassHost();
            default:
                throw new UnsupportedOperationException();
            }
        }
    }

    public class ProcessTask implements Callable<TaskStatus> {
        @Override
        public TaskStatus call() {
            merge();
            buildOsmEntities();
            return new TaskStatus();
        }
        
        private void merge() {
            OsmLayerManager layerManager = context.getComponent(OsmLayerManager.class);
            layerManager.getOsmDataLayer().mergeFrom(dataSet);
            DataSet layerDataset = layerManager.getOsmDataLayer().getDataSet();
            request.getBoundary().getBounds().forEach(bounds -> {
                DataSource ds = new DataSource(bounds, "OSM");
                layerDataset.addDataSource(ds);
            });
        }
        
        private void buildOsmEntities() {
            OsmEntityBuilders entityBuilders = context.getComponent(OsmEntityBuilders.class);
            for (OsmPrimitive p : dataSet.getPrimitives(p -> true)) {
                entityBuilders.forEach(builder -> builder.buildOsmEntity(p));
            }
        }
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    @Override
    public void cancel() {
        osmServerReaders.forEach(OsmServerReader::cancel);
    }
}
