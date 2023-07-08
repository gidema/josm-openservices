package org.openstreetmap.josm.plugins.ods.io;

import java.util.Collections;
import java.util.List;

import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.tools.I18n;

/**
 * This downloader fetches modifications from a server that monitors the differences between open data features and OSM objects. 
 * It also creates a set of bounding boxes, using a buffer around the modified features. This determines the download area for the
 *     open data features and OSM objects, including nearby neighbors.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class FetchModificationsDownloader {
    private final OdsContext context;

    private final List<OpenDataLayerDownloader> layerDownloaders;

    public FetchModificationsDownloader(OdsContext context) {
        super();
        this.context = context;
        OpenDataLayerDownloader odlayerDownloader = new OpenDataLayerDownloader(context);
        this.layerDownloaders = Collections.singletonList(odlayerDownloader);
    }

    public OdsContext getContext() {
        return context;
    }

    public void run(ProgressMonitor pm) {
        pm.indeterminateSubTask(I18n.tr("Fetching modifications"));
        TaskStatus status = fetchModifications();
        if (Downloader.checkErrors(status, pm)) {
            pm.finishTask();
            return;
        }
        

        DownloadRequest request = context.getComponent(DownloadRequest.class);
        context.register(DownloadResponse.class, new DownloadResponse(request), true);

        pm.indeterminateSubTask(I18n.tr("Processing modifications"));
        status = process();
        if (Downloader.checkErrors(status, pm)) {
            pm.finishTask();
            return;
        }
        
        ModifiedAreasFactory areasFactory = new ModifiedAreasFactory();
        areasFactory.run(context);
        
        DownloadRequest actualRequest = new DownloadRequest(request.getDownloadTime(), areasFactory.getBoundary());
        context.register(DownloadRequest.class, actualRequest, true);
        int i = 0;
    }

    private TaskStatus fetchModifications() {
        return Downloader.runTasks(Downloader.getFetchTasks(layerDownloaders));
    }

    /**
     * Run the tasks that depend on more than one entity store.
     *
     */
    protected TaskStatus process() {
        TaskStatus taskStatus = Downloader.runTasks(Downloader.getProcessTasks(layerDownloaders));
        return taskStatus;
    }

    public void cancel() {
        for (LayerDownloader downloader : layerDownloaders) {
            downloader.cancel();
        }
//        executorService.shutdownNow();
    }
}
