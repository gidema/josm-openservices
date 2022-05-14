package org.openstreetmap.josm.plugins.ods.io;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.tools.I18n;

/**
 * Downloader that retrieves data from open data sources about modified (added / deleted) entities.. Currently only a OSM source
 * and a single OpenData source are supported.
 * The
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class ModificationsDownloader {
    private static final int NTHREADS = 10;

    private final OdsContext context;

    private ExecutorService executorService;

    private OpenDataLayerDownloader openDataLayerDownloader;

    public ModificationsDownloader(OdsContext context) {
        super();
        this.context = context;
    }

    public void run(ProgressMonitor pm) {
        String operationMode = context.getParameter(ODS.OPERATION_MODE);
        
        pm.indeterminateSubTask(I18n.tr("Setup"));
        setup();

        pm.indeterminateSubTask(I18n.tr("Preparing"));
        TaskStatus status = prepare();
        if (Downloader.checkErrors(status, pm)) {
            pm.finishTask();
            return;
        }
        
        pm.indeterminateSubTask(I18n.tr("Downloading"));
        status = download();
        if (Downloader.checkErrors(status, pm)) {
            pm.finishTask();
            return;
        }
        DownloadRequest request = context.getComponent(DownloadRequest.class);
        context.register( DownloadResponse.class, new DownloadResponse(request), true);

        pm.indeterminateSubTask(I18n.tr("Processing data"));
        status = process();
        if (Downloader.checkErrors(status, pm)) {
            pm.finishTask();
            return;
        }
    }

    /**
     * Setup the download jobs. One job for the Osm data and one for imported data.
     * Setup the download tasks. Maybe more than 1 per job.
     */
    private void setup() {
        openDataLayerDownloader = context.getComponent(OpenDataLayerDownloader.class);
    }

    private TaskStatus prepare() {
        List<FutureTask<TaskStatus>> tasks = Collections.singletonList(openDataLayerDownloader.getPrepareTask());
        return Downloader.runTasks(tasks);
    }

    private TaskStatus download() {
        List<FutureTask<TaskStatus>> tasks = Collections.singletonList(openDataLayerDownloader.getFetchTask());
        return Downloader.runTasks(tasks);
    }

    /**
     * Run the tasks that depend on more than one entity store.
     *
     */
    protected TaskStatus process() {
        List<FutureTask<TaskStatus>> tasks = Collections.singletonList(openDataLayerDownloader.getProcessTask());
        return Downloader.runTasks(tasks);
    }

    public void cancel() {
        openDataLayerDownloader.cancel();
        executorService.shutdownNow();
    }
}
