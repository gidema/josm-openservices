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
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
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

    private MainDownloader downloader;
    private LocalDateTime startDate;
    private boolean cancelled = false;
    private Boundary boundary;
    private final SlippyMapDownloadDialog slippyDialog;
    private final FixedBoundsDownloadDialog fixedDialog;

    public OdsDownloadAction(OdsContext context) {
        super(context, "Download", ImageProvider.get("download"));
        slippyDialog = new SlippyMapDownloadDialog(context);
        fixedDialog = new FixedBoundsDownloadDialog(context);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.downloader = new MainDownloader(getContext());
        run();
    }

    public void run() {
        cancelled = false;
        boundary = getBoundary();
        startDate = LocalDateTime.now();
        if (!cancelled) {
            DownloadTask task = new DownloadTask(getContext());
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
//        downloadOsm = dialog.cbDownloadOSM.isSelected();
//        downloadOpenData = dialog.cbDownloadODS.isSelected();
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
        // Check if the active layer is the Ods Polygon datalayer
        if (activeLayer.getName().equals("ODS Polygons")) {
            OsmDataLayer layer = (OsmDataLayer) activeLayer;
            if (layer.getDataSet().getAllSelected().size() != 1) {
                return null;
            }
            // If the selected object is a closed way than we can assume is was intended to be used as a polygon for
            // the download area
            OsmPrimitive primitive = layer.getDataSet().getAllSelected().iterator().next();
            if (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY) {
                return new Boundary((Way)primitive);
            }
        }
        return null;
    }

    private class DownloadTask extends PleaseWaitRunnable {
        private final OdsContext context;

        public DownloadTask(OdsContext context) {
            super(tr("Downloading data"));
            this.context = context;
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
            DownloadRequest request = new DownloadRequest(startDate, boundary);
            context.register(DownloadRequest.class, request, true);
            downloader.run(getProgressMonitor());
        }

        @Override
        protected void finish() {
            MainApplication.getLayerManager().setActiveLayer(getContext().getComponent(OdLayerManager.class).getOsmDataLayer());
        }
    }
}
