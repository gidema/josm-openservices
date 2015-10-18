package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.util.Collections;
import java.util.List;

import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.entities.EntitySource;
import org.openstreetmap.josm.plugins.ods.io.DownloadJob;
import org.openstreetmap.josm.plugins.ods.io.Downloader;
import org.openstreetmap.josm.plugins.ods.io.Status;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.tasks.Task;

@Deprecated
public class OsmDownloadJob implements DownloadJob {
    private final InternalDataLayer dataLayer;
    private final OsmDownloader downloader;
    private final List<Task> tasks;
    private final Status status = new Status();
//    private Context ctx;
//    private EntitySource entitySource;

    public OsmDownloadJob(InternalDataLayer dataLayer, OsmDownloader downloader, List<Task> tasks) {
        super();
        this.dataLayer = dataLayer;
        this.downloader = downloader;
        this.tasks = tasks;
        this.tasks.add(new MergeTask());
    }

    @Override
    public List<? extends Downloader> getDownloaders() {
        return Collections.singletonList(downloader);
    }

//    public void prepare(Context ctx) {
//        this.ctx = ctx;
//        this.entitySource = (EntitySource) ctx.get("entitySource");
//    }
//    
    public void process(Context ctx) {
        for (Task task : tasks) {
            task.run(ctx);
        }
    }
    
    @Override
    public Status getStatus() {
        return status;
    }

    class MergeTask implements Task {

        @Override
        public void run(Context ctx) {
            dataLayer.getOsmDataLayer().mergeFrom(downloader.getDataSet());
            EntitySource entitySource = (EntitySource) ctx.get("entitySource");
            Boundary boundary = entitySource.getBoundary();
            DataSource ds = new DataSource(boundary.getBounds(), "OSM");
            dataLayer.getOsmDataLayer().data.dataSources.add(ds);
        }
    }
}
