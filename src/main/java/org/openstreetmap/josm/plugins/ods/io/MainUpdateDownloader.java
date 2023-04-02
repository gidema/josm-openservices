package org.openstreetmap.josm.plugins.ods.io;

import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;

/**
 * Main downloader for the update process. This downloader only downloads modified areas, which allows to handle larger areas at a time.
 * To do this, we go through these steps:
 * 1. Use a FetchModifications downloader to
 *   - Collect modified features from a server that compares open data features with OSM objects.
 *   - Create a set of bounding boxes, using a buffer around the modified features. This determines the download area for the
 *     open data features and OSM objects, including nearby neighbors.
 * 2. Use the regular dowloader with this bounding boxes to:
 *   - Prepare the actual download
 *   - Download open data features an OSM object from the derived download area
 *   - Process the results
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class MainUpdateDownloader implements MainDownloader {
    private final OdsContext context;

    public MainUpdateDownloader(OdsContext context) {
        super();
        this.context = context;
        
    }

    public OdsContext getContext() {
        return context;
    }

    @Override
    public void run(ProgressMonitor pm) {
        FetchModificationsDownloader modificationsDownloader = new FetchModificationsDownloader(context);
        
        modificationsDownloader.run(pm);
        
        // Determine download area(s) and download request
        MainFetchDownloader mainDownloader = new MainFetchDownloader(context);
        
        mainDownloader.run(pm);
//        computeBboxAndCenterScale(request.getBoundary().getBounds());
        pm.finishTask();
    }

    @Override
    public void cancel() {
        // TODO Auto-generated method stub
        
    }

}
