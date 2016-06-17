package org.openstreetmap.josm.plugins.ods.gui;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.PleaseWaitRunnable;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.BuildingEntityType;
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
    private boolean downloadOsm;
    private boolean downloadOpenData;
    private SlippyMapDownloadDialog slippyDialog;
    private FixedBoundsDownloadDialog fixedDialog;
    
    public OdsDownloadAction(OdsModule module) {
        super(module, "Download", ImageProvider.get("download"));
        slippyDialog = new SlippyMapDownloadDialog(module);
        fixedDialog = new FixedBoundsDownloadDialog(module);
        this.downloader = module.getDownloader();
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
            Main.worker.submit(task);
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
        if (Main.map == null) {
            return null;
        }
        Layer activeLayer = Main.getLayerManager().getActiveLayer();
        // Make sure the active layer is an Osm datalayer
        if (!(activeLayer instanceof OsmDataLayer)) {
            return null;
        }
        // Make sure only one object was selected
        OsmDataLayer layer = (OsmDataLayer) activeLayer;
        if (layer.data.getAllSelected().size() != 1) {
            return null;
        }
        // If the selected object is a closed way an it is not a building
        // than we can assume is was intended to be used as a polygon for
        // the download area
        OsmPrimitive primitive = layer.data.getAllSelected().iterator().next();
        if (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY
            && !BuildingEntityType.IsBuilding.evaluate(primitive)) {
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
            try {
                DownloadRequest request = new DownloadRequest(startDate, boundary,
                    downloadOsm, downloadOpenData);
                downloader.run(getProgressMonitor(), request);
            } catch (ExecutionException|InterruptedException e) {
                throw new OsmTransferException(e);
            }
        }

        @SuppressWarnings("synthetic-access")
        @Override
        protected void finish() {
            if (downloadOpenData) {
                Main.getLayerManager().setActiveLayer(getModule().getOpenDataLayerManager().getOsmDataLayer());
            }
            else {
                Main.getLayerManager().setActiveLayer(getModule().getOsmLayerManager().getOsmDataLayer());
            }
        }
    }
}
