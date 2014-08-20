package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.util.concurrent.Callable;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.BoundingBoxDownloader;
import org.openstreetmap.josm.io.OsmApiException;
import org.openstreetmap.josm.io.OsmServerLocationReader;
import org.openstreetmap.josm.io.OsmServerReader;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.DownloadTask;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.jts.MultiPolygonFilter;
import org.openstreetmap.josm.tools.I18n;

public class InternalDownloadTask implements DownloadTask {
//    final OdsModule module;
    Boundary boundary;
    OsmServerReader osmServerReader;
    private static String overpassQuery = 
        "(node($bbox);rel(bn)->.x;way($bbox);" +
        "node(w)->.x;rel(bw);)";
//    List<Exception> exceptions = new LinkedList<Exception>();
    boolean failed = false;
    boolean cancelled = false;
    Exception exception = null;
    String message = null;
    private DownloadSource downloadSource=  DownloadSource.OSM;
    private DataSet dataSet;

    protected InternalDownloadTask() {
        super();
//        this.module = module;
    }
    
    public void setBoundary(Boundary boundary) {
        this.boundary = boundary;
    }

    public boolean failed() {
        return failed;
    }
    
    @Override
    public boolean cancelled() {
        return cancelled;
    }

    public String getMessage() {
        if (message != null) {
            return message;
        }
        if (exception != null) {
            return exception.getMessage();
        }
        return null;
    }
    
    public Exception getException() {
        return exception;
    }
    
    public DataSet getDataSet() {
        return dataSet;
    }
    
    @Override
    public Callable<Object> getPrepareCallable() {
        return new Callable<Object>() {

            @Override
            public Object call() {
                switch (downloadSource) {
                case OSM:
                    osmServerReader = new BoundingBoxDownloader(boundary.getBounds());
                    break;
                case OVERPASS:
                    String url = Overpass.getURL(overpassQuery, boundary);
                    osmServerReader = new OsmServerLocationReader(url);
                    break;
                }
                return null;
            }
        };
    }

    @Override
    public Callable<?> getDownloadCallable() {
        return new Callable<Object>() {

            @Override
            public Object call() {
                try {
                    if (cancelled)
                        return null;
                    dataSet = parseDataSet();
                    if (downloadSource == DownloadSource.OSM) {
                        MultiPolygonFilter filter = new MultiPolygonFilter(boundary.getMultiPolygon());
                        dataSet = filter.filter(dataSet);
                    }
                    if (dataSet.allPrimitives().isEmpty()) {
                        cancelled = true;
                        message = I18n.tr("The selected download area contains no OSM objects");
                        return null;
                    }
                }
                catch(Exception e) {
                    failed = true;
                    if (cancelled()) {
                        Main.info(I18n.tr("Ignoring exception because download has been canceled. Exception was: {0}", e.toString()));
                        return null;
                    }
                    if (e instanceof OsmApiException) {
                        if ( ((OsmApiException) e).getResponseCode() == 400) {
                            message = I18n.tr("You tried to download too much Openstreetmap data. Please select a smaller download area.");
                            return null;
                        }
                    }
                    exception = e;
                }
                return null;
            }
                
            protected DataSet parseDataSet() throws OsmTransferException {
                return osmServerReader.parseOsm(NullProgressMonitor.INSTANCE);
            }

        };
    }

    @Override
    public void operationCanceled() {
        cancelled = true;
        if (osmServerReader != null) {
            osmServerReader.cancel();
        }
    }
    
    static enum DownloadSource {
        OSM,
        OVERPASS;
    }
}
