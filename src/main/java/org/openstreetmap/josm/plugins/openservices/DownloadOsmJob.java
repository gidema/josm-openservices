package org.openstreetmap.josm.plugins.openservices;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.openstreetmap.josm.actions.downloadtasks.DownloadOsmTask;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.openservices.entities.Entity;

public class DownloadOsmJob implements DownloadJob {
    final OdsWorkingSet workingSet;
    final Bounds bounds;
    String overpassQuery;
    List<Exception> exceptions = new LinkedList<Exception>();

    protected DownloadOsmJob(OdsWorkingSet workingSet, Bounds bounds) {
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
                return null;
            }
        };
    }

    @Override
    public Callable<?> getDownloadCallable() {
        return new Callable<Object>() {

            @Override
            public Object call() {
                DownloadOsmTask osmTask = new DownloadOsmTask();
                Future<?> osmFuture;
                if (overpassQuery != null) {
                    String url = getOverpassUrl(overpassQuery, bounds);
                    osmTask = new DownloadOsmTask();
                    osmFuture = osmTask.loadUrl(false, url, null);
                } else {
                    osmTask = new DownloadOsmTask();
                    osmFuture = osmTask.download(false, bounds, null);
                }
                // Wait for the task to finish ?
                // osmFuture.get();
                return null;
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

    @Override
    public Set<Entity> getNewEntities() {
        // TODO Auto-generated method stub
        return null;
    }
}
