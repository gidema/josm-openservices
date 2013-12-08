package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.BoundingBoxDownloader;
import org.openstreetmap.josm.io.OsmTransferCanceledException;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.DownloadTask;
import org.openstreetmap.josm.plugins.ods.OdsWorkingSet;

public class InternalDownloadTask implements DownloadTask {
    final OdsWorkingSet workingSet;
    final Bounds bounds;
    BoundingBoxDownloader bbDownloader;
    String overpassQuery;
    List<Exception> exceptions = new LinkedList<Exception>();

    protected InternalDownloadTask(OdsWorkingSet workingSet, Bounds bounds) {
        super();
        this.workingSet = workingSet;
        this.bounds = bounds;
    }

    @Override
    public Callable<?> getPrepareCallable() {
        return new Callable<Object>() {

            @Override
            public Object call() {
                overpassQuery = workingSet.getOsmQuery();
                bbDownloader = new BoundingBoxDownloader(bounds);
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
//                    if (isCanceled())
//                        return;
                    DataSet dataSet = parseDataSet();
                    workingSet.internalDataLayer.mergeFrom(dataSet);
                }
                catch(Exception e) {
//                    if (isCanceled()) {
//                        Main.info(tr("Ignoring exception because download has been canceled. Exception was: {0}", e.toString()));
//                        return;
//                    }
                    if (e instanceof OsmTransferCanceledException) {
//                        setCanceled(true);
                        return null;
                    } else if (e instanceof OsmTransferException) {
//                        rememberException(e);
                    } else {
//                        rememberException(new OsmTransferException(e));
                    }
                }
                return null;
            }
                
            protected DataSet parseDataSet() throws OsmTransferException {
                return bbDownloader.parseOsm(NullProgressMonitor.INSTANCE);
            }

        };
    }

    // @Override
    // public OdsFeatureSet getFeatureSet() {
    // return null;
    // }
    //
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
