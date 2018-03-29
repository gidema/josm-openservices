package org.openstreetmap.josm.plugins.ods.io;

import java.net.MalformedURLException;
import java.net.UnknownHostException;

import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.OsmApiException;
import org.openstreetmap.josm.io.OsmServerReader;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntitiesBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.jts.MultiPolygonFilter;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

public class OsmLayerDownloader implements LayerDownloader {
    private DownloadRequest request;
    @SuppressWarnings("unused")
    private DownloadResponse response;
    private Status status = new Status();
    private DownloadSource downloadSource =  DownloadSource.OVERPASS;
    private OsmServerReader osmServerReader;
    private OsmLayerManager layerManager;
    private OsmEntitiesBuilder entitiesBuilder;

    private OsmHost host;
    private DataSet dataSet;

    static enum DownloadSource {
        OSM,
        OVERPASS;
    }
    
    public OsmLayerDownloader(OdsModule module) {
        super();
        this.layerManager = module.getOsmLayerManager();
        this.entitiesBuilder = layerManager.getEntitiesBuilder();
        
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setResponse(DownloadResponse response) {
        this.response = response;
    }


    @Override
    public void setup(DownloadRequest request) {
        this.request = request;
        status.clear();
        switch (downloadSource) {
        case OSM:
            host = new PlainOsmHost();
            break;
        case OVERPASS:
            host = new OverpassHost();
            break;
        default:
            return;
        }
        try {
            osmServerReader = host.getServerReader(request);
        } catch (MalformedURLException e) {
            status.setException(e);
        }
    }

    @Override
    public void download() {
        try {
            dataSet = parseDataSet();
            if (downloadSource == DownloadSource.OSM) {
                MultiPolygonFilter filter = new MultiPolygonFilter(request.getBoundary().getMultiPolygon());
                dataSet = filter.filter(dataSet);
            }
        }
        catch(OsmTransferException e) {
            if (status.isCancelled()) {
                Logging.info(I18n.tr("Ignoring exception because download has been canceled. Exception was: {0}", e.toString()));
                return;
            }
            status.setFailed(true);
            if (e instanceof OsmApiException) {
                switch (((OsmApiException) e).getResponseCode()) {
                case 400:
                    status.setMessage(I18n.tr("You tried to download too much Openstreetmap data. Please select a smaller download area."));
                    return;
                case 404:
                    status.setMessage(I18n.tr("No OSM server could be found at this location: {0}", 
                        host.getHostString().toString()));
                    return;
                default:
                    status.setMessage(I18n.tr(e.getMessage()));
                    return;
                }
            }
            else if (e.getCause() instanceof UnknownHostException) {
                status.setMessage(I18n.tr("Could not connect to OSM server ({0}). Please check your Internet connection.",  host.getHostString()));
                return;
            }
            status.setException(e);
        }
    }

    @Override
    public void prepare() {
        // Nothing to prepare
    }


    @Override
    public void process() {
        merge();
        entitiesBuilder.build();
    }

    private void merge() {
        layerManager.getOsmDataLayer().mergeFrom(dataSet);
        Boundary boundary = request.getBoundary();
        DataSource ds = new DataSource(boundary.getBounds(), "OSM");
        layerManager.getOsmDataLayer().getDataSet().addDataSource(ds);

    }
    private DataSet parseDataSet() throws OsmTransferException {
        return osmServerReader.parseOsm(NullProgressMonitor.INSTANCE);
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    @Override
    public void cancel() {
        osmServerReader.cancel();
        status.setCancelled(true);
    }
}
