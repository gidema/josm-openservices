package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.util.Collections;
import java.util.List;

import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.plugins.ods.io.DownloadJob;
import org.openstreetmap.josm.plugins.ods.io.Downloader;
import org.openstreetmap.josm.plugins.ods.io.Status;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.tasks.Task;

public class OsmDownloadJob implements DownloadJob {
    private final InternalDataLayer dataLayer;
    private final OsmDownloader downloader;
    private final List<Task> tasks;
    private Boundary boundary;
    private final Status status = new Status();

    public OsmDownloadJob(InternalDataLayer dataLayer, OsmDownloader downloader, List<Task> tasks) {
        super();
        this.dataLayer = dataLayer;
        this.downloader = downloader;
        this.tasks = tasks;
        this.tasks.add(new MergeTask());
    }

    @Override
    public void setBoundary(Boundary boundary) {
        this.boundary = boundary;
        downloader.setBoundary(boundary);
    }
    
    @Override
    public List<? extends Downloader> getDownloaders() {
        return Collections.singletonList(downloader);
    }

    public void process() {
        for (Task task : tasks) {
            task.run();
        }
    }
    
    @Override
    public Status getStatus() {
        return status;
    }

    class MergeTask implements Task {

        @Override
        public void run() {
            dataLayer.getOsmDataLayer().mergeFrom(downloader.getDataSet());
            DataSource ds = new DataSource(boundary.getBounds(), "OSM");
            dataLayer.getOsmDataLayer().data.dataSources.add(ds);
        }       
    }
}
