package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.util.Locale;
import java.util.concurrent.Callable;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.BoundingBoxDownloader;
import org.openstreetmap.josm.io.OsmApiException;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.DownloadTask;
import org.openstreetmap.josm.plugins.ods.OdsWorkingSet;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.tools.I18n;

public class InternalDownloadTask implements DownloadTask {
    final OdsWorkingSet workingSet;
    final Boundary boundary;
    BoundingBoxDownloader bbDownloader;
    String overpassQuery;
//    List<Exception> exceptions = new LinkedList<Exception>();
    boolean failed = false;
    boolean cancelled = false;
    Exception exception = null;
    String errorMessage = null;

    protected InternalDownloadTask(OdsWorkingSet workingSet, Boundary boundary) {
        super();
        this.workingSet = workingSet;
        this.boundary = boundary;
    }

    public boolean failed() {
        return failed;
    }
    
    @Override
    public boolean cancelled() {
        return cancelled;
    }

    public String getErrorMessage() {
        if (errorMessage != null) {
            return errorMessage;
        }
        if (exception != null) {
            return exception.getMessage();
        }
        return null;
    }
    
    public Exception getException() {
        return exception;
    }
    
    @Override
    public Callable<Object> getPrepareCallable() {
        return new Callable<Object>() {

            @Override
            public Object call() {
                overpassQuery = workingSet.getOsmQuery();
                bbDownloader = new BoundingBoxDownloader(boundary.getBounds());
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
                    DataSet dataSet = parseDataSet();
                    if (!boundary.isRectangular()) {
                        boundary.filter(dataSet);
                    }
                    workingSet.internalDataLayer.getOsmDataLayer().mergeFrom(dataSet);
                }
                catch(Exception e) {
                    failed = true;
//                    if (isCanceled()) {
//                        Main.info(tr("Ignoring exception because download has been canceled. Exception was: {0}", e.toString()));
//                        return;
//                    }
                    if (e instanceof OsmApiException) {
                        if ( ((OsmApiException) e).getResponseCode() == 400) {
                            errorMessage = I18n.tr("You tried to download too much Openstreetmap data. Please select a smaller download area.");
                        }
                    }
                    exception = e;
                }
                return null;
            }
                
            protected DataSet parseDataSet() throws OsmTransferException {
                return bbDownloader.parseOsm(NullProgressMonitor.INSTANCE);
            }

        };
    }

    @Override
    public void operationCanceled() {
        cancelled = true;
        if (bbDownloader != null) {
            bbDownloader.cancel();
        }
    }

    static String getOverpassUrl(String query, Bounds bounds) {
        String host = "http://overpass-api.de/api";
        String bbox = String.format(Locale.ENGLISH, "%f,%f,%f,%f", bounds
                .getMin().getY(), bounds.getMin().getX(), bounds.getMax()
                .getY(), bounds.getMax().getX());
        String q = query.replaceAll("\\$bbox", bbox);
        q = q.replaceAll("\\{\\{bbox\\}\\}", bbox);
        q = q.replace(";$", "");
        return String.format("%s/interpreter?data=%s;out meta;", host, q);
    }
}
