package org.openstreetmap.josm.plugins.ods.entities.external;

import java.util.List;

import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.io.DownloadJob;
import org.openstreetmap.josm.plugins.ods.io.Downloader;
import org.openstreetmap.josm.plugins.ods.io.Status;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.tasks.Task;

public class GeotoolsDownloadJob implements DownloadJob {
    private final ExternalDataLayer dataLayer;
    private Boundary boundary;
    private final List<Downloader> downloaders;
    private final List<Task> tasks;
    private final Status status = new Status();

    public GeotoolsDownloadJob(ExternalDataLayer dataLayer, List<Downloader> downloaders, List<Task> tasks) {
        this.dataLayer = dataLayer;
        this.downloaders = downloaders;
        this.tasks = tasks;
    }
    
    @Override
    public List<? extends Downloader> getDownloaders() {
        return downloaders;
    }

    @Override
    public void setBoundary(Boundary boundary) {
        for (Downloader downloader : downloaders) {
            downloader.setBoundary(boundary);
        }
        this.boundary = boundary;
    }
    
    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void process() {
        for (Task task : tasks) {
            task.run();
        }
        DataSource ds = new DataSource(boundary.getBounds(), "Import");
        OsmDataLayer osmDataLayer = dataLayer.getOsmDataLayer();
        osmDataLayer.data.dataSources.add(ds);
    }
}
