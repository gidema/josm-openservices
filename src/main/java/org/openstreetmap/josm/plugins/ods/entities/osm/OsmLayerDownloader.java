package org.openstreetmap.josm.plugins.ods.entities.osm;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.BoundingBoxDownloader;
import org.openstreetmap.josm.io.OsmApiException;
import org.openstreetmap.josm.io.OsmServerLocationReader;
import org.openstreetmap.josm.io.OsmServerReader;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.io.LayerDownloader;
import org.openstreetmap.josm.plugins.ods.io.Status;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.jts.MultiPolygonFilter;
import org.openstreetmap.josm.tools.I18n;

public class OsmLayerDownloader implements LayerDownloader {
    private DownloadRequest request;
    @SuppressWarnings("unused")
    private DownloadResponse response;
    private Status status = new Status();
    private DownloadSource downloadSource=  DownloadSource.OSM;
    private OsmServerReader osmServerReader;
    private OsmLayerManager layerManager;
    private OsmEntitiesBuilder entitiesBuilder;

    private static String overpassQuery = 
        "(node($bbox);rel(bn)->.x;way($bbox);" +
        "node(w)->.x;rel(bw);)";
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

    //    @Override
//    public void setBoundary(Boundary boundary) {
//        this.boundary = boundary;
//    }
//
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
//        this.entitySource = (EntitySource) ctx.get("entitySource");
        status.clear();
        switch (downloadSource) {
        case OSM:
            osmServerReader = new BoundingBoxDownloader(request.getBoundary().getBounds());
            break;
        case OVERPASS:
            String url = Overpass.getURL(overpassQuery, request.getBoundary());
            osmServerReader = new OsmServerLocationReader(url);
            break;
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
            if (dataSet.allPrimitives().isEmpty()) {
                status.setCancelled(true);
                status.setMessage(I18n.tr("The selected download area contains no OSM objects"));
                return;
            }
        }
        catch(Exception e) {
            if (status.isCancelled()) {
                Main.info(I18n.tr("Ignoring exception because download has been canceled. Exception was: {0}", e.toString()));
                return;
            }
            status.setFailed(true);
            if (e instanceof OsmApiException) {
                if ( ((OsmApiException) e).getResponseCode() == 400) {
                    status.setMessage(I18n.tr("You tried to download too much Openstreetmap data. Please select a smaller download area."));
                    return;
                }
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
        layerManager.getOsmDataLayer().data.dataSources.add(ds);

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
