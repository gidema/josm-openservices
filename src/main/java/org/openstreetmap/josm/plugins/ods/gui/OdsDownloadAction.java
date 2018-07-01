package org.openstreetmap.josm.plugins.ods.gui;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.LocalDateTime;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.PleaseWaitRunnable;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;
import org.openstreetmap.josm.plugins.ods.io.MainDownloader;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.tools.ImageProvider;
import org.xml.sax.SAXException;

public class OdsDownloadAction extends OdsAction {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final MainDownloader downloader;
    private LocalDateTime startDate;
    private boolean cancelled = false;
    private Boundary boundary;
    private boolean downloadOsm;
    private boolean downloadOpenData;
    private final SlippyMapDownloadDialog slippyDialog;
    private final FixedBoundsDownloadDialog fixedDialog;

    public final OdLayerManager odLayerManager;

    public OdsDownloadAction(OdLayerManager odLayerManager, MainDownloader downloader, String moduleName) {
        super("Download", ImageProvider.get("download"));
        this.odLayerManager = odLayerManager;
        slippyDialog = new SlippyMapDownloadDialog(moduleName);
        fixedDialog = new FixedBoundsDownloadDialog(moduleName);
        this.downloader = downloader;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        run();
    }

    public void run() {
        cancelled = false;
        boundary = getBoundary();
        startDate = LocalDateTime.now();
        if (!cancelled) {
            DownloadTask task = new DownloadTask();
            MainApplication.worker.submit(task);
        }
    }

    private Boundary getBoundary() {
        boundary = getPolygonBoundary();
        boolean selectArea = (boundary == null);
        AbstractDownloadDialog dialog;
        if (selectArea) {
            dialog = slippyDialog;
        }
        else {
            dialog = fixedDialog;
        }
        dialog.restoreSettings();
        dialog.setVisible(true);
        if (dialog.isCanceled()) {
            cancelled = true;
            return null;
        }
        dialog.rememberSettings();
        downloadOsm = dialog.cbDownloadOSM.isSelected();
        downloadOpenData = dialog.cbDownloadODS.isSelected();
        if (selectArea) {
            boundary = new Boundary(dialog.getSelectedDownloadArea());
        }
        return boundary;
    }

    private static Boundary getPolygonBoundary() {
        if (MainApplication.getMap() == null) {
            return null;
        }
        Layer activeLayer = MainApplication.getLayerManager().getActiveLayer();
        // Make sure the active layer is an Osm datalayer
        if (!(activeLayer instanceof OsmDataLayer)) {
            return null;
        }
        // Make sure only one object was selected
        OsmDataLayer layer = (OsmDataLayer) activeLayer;
        if (layer.getDataSet().getAllSelected().size() != 1) {
            return null;
        }
        // If the selected object is a closed way and it is not a building
        // than we can assume is was intended to be used as a polygon for
        // the download area
        OsmPrimitive primitive = layer.getDataSet().getAllSelected().iterator().next();
        if (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY
                && !OsmBuilding.IsBuilding(primitive)) {
            return new Boundary((Way)primitive);
        }
        return null;
    }

    private class DownloadTask extends PleaseWaitRunnable {

        public DownloadTask() {
            super(tr("Downloading data"));
        }

        @SuppressWarnings("synthetic-access")
        @Override
        protected void cancel() {
            downloader.cancel();
        }

        @SuppressWarnings("synthetic-access")
        @Override
        protected void realRun() throws SAXException, IOException,
        OsmTransferException {
            DownloadRequest request = new DownloadRequest(startDate, boundary,
                    downloadOsm, downloadOpenData);
            downloader.run(getProgressMonitor(), request);
        }

        @Override
        protected void finish() {
            MainApplication.getLayerManager().setActiveLayer(odLayerManager.getOsmDataLayer());
        }
    }
}
