package org.openstreetmap.josm.plugins.ods.io;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.MainLayerManager;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.matching.Matchers;
import org.openstreetmap.josm.tools.I18n;

/**
 * Main downloader that retrieves data from multiple sources. Currently only a OSM source
 * and a single OpenData source are supported.
 * The
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class MainDownloader {
    private final OdsContext context;

    private ExecutorService executorService;

    private List<LayerDownloader> layerDownloaders;

    public MainDownloader(OdsContext context) {
        super();
        this.context = context;
    }

    public OdsContext getContext() {
        return context;
    }

    public void run(ProgressMonitor pm) {
        String operationMode = context.getParameter(ODS.OPERATION_MODE);
        
        pm.indeterminateSubTask(I18n.tr("Setup"));
        setup();
        if (operationMode.equals("Update")) {
            /*
             * In Update mode, we first collect the modified entities only. These will be used to determine a collection of
             * bounding boxes for the other download operations.
             */
            pm.indeterminateSubTask(I18n.tr("Download"));
            setup();
        }
        // Switch to the Open data layer before downloading.
        MainLayerManager layerManager = MainApplication.getLayerManager();
        layerManager.setActiveLayer(getContext().getComponent(OdLayerManager.class).getOsmDataLayer());

        pm.indeterminateSubTask(I18n.tr("Preparing"));
        TaskStatus status = prepare();
        if (Downloader.checkErrors(status, pm)) {
            pm.finishTask();
            return;
        }
        
        pm.indeterminateSubTask(I18n.tr("Downloading"));
        status = fetch();
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

        computeBboxAndCenterScale(request.getBoundary().getBounds());
        pm.finishTask();
    }

    /**
     * Setup the download jobs. One job for the Osm data and one for imported data.
     * Setup the download tasks. Maybe more than 1 per job.
     */
    private void setup() {
        OsmLayerDownloader osmLayerDownloader = context.getComponent(OsmLayerDownloader.class);
        OpenDataLayerDownloader openDataLayerDownloader = context.getComponent(OpenDataLayerDownloader.class);
        this.layerDownloaders = Arrays.asList(osmLayerDownloader, openDataLayerDownloader);
        layerDownloaders.forEach(ld -> ld.setup(context));
    }

    private TaskStatus prepare() {
        return Downloader.runTasks(Downloader.getPrepareTasks(layerDownloaders));
    }

    private TaskStatus fetch() {
        return Downloader.runTasks(Downloader.getFetchTasks(layerDownloaders));
    }

    /**
     * Run the tasks that depend on more than one entity store.
     *
     */
    protected TaskStatus process() {
        TaskStatus taskStatus = Downloader.runTasks(Downloader.getProcessTasks(layerDownloaders));
        Matchers matchers = context.getComponent(Matchers.class);
        matchers.forEach(Matcher::run);
        return taskStatus;
    }

    protected static void computeBboxAndCenterScale(Collection<Bounds> bounds) {
        BoundingXYVisitor v = new BoundingXYVisitor();
        
        if (bounds != null && !bounds.isEmpty()) {
            bounds.forEach(v::visit);
            MainApplication.getMap().mapView.zoomTo(v.getBounds());
        }
    }

    public void cancel() {
        for (LayerDownloader downloader : layerDownloaders) {
            downloader.cancel();
        }
        executorService.shutdownNow();
    }
}
