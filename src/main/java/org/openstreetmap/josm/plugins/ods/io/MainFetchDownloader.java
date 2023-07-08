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
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.matching.Matchers;
import org.openstreetmap.josm.tools.I18n;

/**
 * MainDownloader implementation that retrieves all data for an area, regardless of changes.
 *  *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class MainFetchDownloader implements MainDownloader {
    private final OdsContext context;

    private ExecutorService executorService;

    private List<LayerDownloader> layerDownloaders;

    public MainFetchDownloader(OdsContext context) {
        super();
        this.context = context;
        OsmLayerDownloader osmLayerDownloader = new OsmLayerDownloader(context);
        OpenDataLayerDownloader odLayerDownloader = new OpenDataLayerDownloader(context);
        this.layerDownloaders = Arrays.asList(osmLayerDownloader, odLayerDownloader);
    }

    public OdsContext getContext() {
        return context;
    }

    @Override
    public void run(ProgressMonitor pm) {
        
        pm.indeterminateSubTask(I18n.tr("Setup"));
        setup();

        // Switch to the Open data layer before downloading.
        MainLayerManager layerManager = MainApplication.getLayerManager();
        layerManager.setActiveLayer(getContext().getComponent(OdLayerManager.class).getOsmDataLayer());

        pm.indeterminateSubTask(I18n.tr("Preparing"));
        TaskStatus status = prepare();
        if (Downloader.checkErrors(status, pm)) {
            pm.finishTask();
            return;
        }
        
        pm.indeterminateSubTask(I18n.tr("Fetching data"));
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

    @Override
    public void cancel() {
        for (LayerDownloader downloader : layerDownloaders) {
            downloader.cancel();
        }
        executorService.shutdownNow();
    }
}
