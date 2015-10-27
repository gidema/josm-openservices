package org.openstreetmap.josm.plugins.ods.entities.osm;

import java.util.List;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.BoundingBoxDownloader;
import org.openstreetmap.josm.io.OsmApiException;
import org.openstreetmap.josm.io.OsmServerLocationReader;
import org.openstreetmap.josm.io.OsmServerReader;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.entities.EntitySource;
import org.openstreetmap.josm.plugins.ods.io.Downloader;
import org.openstreetmap.josm.plugins.ods.io.Status;
import org.openstreetmap.josm.plugins.ods.jts.MultiPolygonFilter;
import org.openstreetmap.josm.tools.I18n;

@Deprecated
public class OsmDownloader implements Downloader {
    private Status status = new Status();
    private EntitySource entitySource;
    private DownloadSource downloadSource=  DownloadSource.OSM;
    private OsmServerReader osmServerReader;
    private List<OsmEntityBuilder<?>> entityBuilders;

    private static String overpassQuery = 
        "(node($bbox);rel(bn)->.x;way($bbox);" +
        "node(w)->.x;rel(bw);)";
    private DataSet dataSet;

    static enum DownloadSource {
        OSM,
        OVERPASS;
    }
    
    public OsmDownloader(List<OsmEntityBuilder<?>> entityBuilders) {
        super();
        this.entityBuilders = entityBuilders;
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
    public void prepare(Context ctx) {
        this.entitySource = (EntitySource) ctx.get("entitySource");
        status.clear();
        switch (downloadSource) {
        case OSM:
            osmServerReader = new BoundingBoxDownloader(entitySource.getBoundary().getBounds());
            break;
        case OVERPASS:
            String url = Overpass.getURL(overpassQuery, entitySource.getBoundary());
            osmServerReader = new OsmServerLocationReader(url);
            break;
        }
    }

    @Override
    public void download() {
        try {
            dataSet = parseDataSet();
            if (downloadSource == DownloadSource.OSM) {
                MultiPolygonFilter filter = new MultiPolygonFilter(entitySource.getBoundary().getMultiPolygon());
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
    public void process() {
        for (OsmPrimitive primitive : dataSet.allPrimitives()) {
            for (OsmEntityBuilder<?> builder : entityBuilders) {
                builder.buildOsmEntity(primitive);
            }
        }
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
    }
}
