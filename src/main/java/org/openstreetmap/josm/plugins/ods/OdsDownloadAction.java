package org.openstreetmap.josm.plugins.ods;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.PleaseWaitRunnable;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.tools.ImageProvider;
import org.xml.sax.SAXException;

public class OdsDownloadAction extends AbstractAction {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private boolean cancelled = false;
    private boolean downloadOsm;
    private boolean downloadOds;
    
    public OdsDownloadAction() {
        super("Download", ImageProvider.get("download"));
        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        run();
    }
    
    public void run() {
        cancelled = false;
        Boundary boundary = getBoundary();
        if (!cancelled) {
            DownloadTask task = new DownloadTask(boundary, downloadOsm, downloadOds);
            Main.worker.submit(task);

        }
//        try {
//            workingSet.download(boundary, true);
//        } catch (ExecutionException e1) {
//            JOptionPane.showMessageDialog(Main.parent, e1.getMessage(),
//                    tr("Error during download"), JOptionPane.ERROR_MESSAGE);
//        } catch (InterruptedException e1) {
//            JOptionPane.showMessageDialog(Main.parent, e1.getMessage(),
//                    tr("Error during download"), JOptionPane.ERROR_MESSAGE);
//        }
    }

    private Boundary getBoundary() {
        Boundary boundary = getPolygonBoundary();
        boolean selectArea = (boundary == null);
        AbstractDownloadDialog dialog;
        if (selectArea) {
            dialog = SlippyMapDownloadDialog.getInstance();
        }
        else {
            dialog = FixedBoundsDownloadDialog.getInstance();
        }
        dialog.restoreSettings();
        dialog.setVisible(true);
        if (dialog.isCanceled()) {
            cancelled = true;
            return null;
        }
        dialog.rememberSettings();
        downloadOsm = dialog.cbDownloadOSM.isSelected();
        downloadOds = dialog.cbDownloadODS.isSelected();
        if (selectArea) {
            boundary = new Boundary(dialog.getSelectedDownloadArea());
        }
        return boundary;
    }
    
    private Boundary getPolygonBoundary() {
        if (Main.map == null) {
            return null;
        }
        Layer activeLayer = Main.map.mapView.getActiveLayer();
        if (!(activeLayer instanceof OsmDataLayer)) {
            return null;
        }
        OsmDataLayer layer = (OsmDataLayer) activeLayer;
        if (layer.data.getAllSelected().size() != 1) {
            return null;
        }
        OsmPrimitive primitive = layer.data.getAllSelected().iterator().next();
        Boundary boundary;
        if (primitive.getDisplayType() != OsmPrimitiveType.CLOSEDWAY) {
            boundary = null;
        }
        else {
            boundary = new Boundary((Way)primitive);
            if (!boundary.getPolygon().isValid()) {
                boundary = null;
            }
        }
        if (boundary == null) {
            JOptionPane.showMessageDialog(Main.parent, tr("The selected area is not a valid Polygon"),
                    tr("Invalid polygon"), JOptionPane.ERROR_MESSAGE);
        }
        return boundary;
    }
    
    private class DownloadTask extends PleaseWaitRunnable {
        private Boundary boundary;
        private boolean downloadOsm;
        private boolean downloadOds;
        private OdsDownloader downloader;
        
        public DownloadTask(Boundary boundary, boolean downloadOsm, boolean downloadOds) {
            super(tr("Downloading data"));
            this.boundary = boundary;
            this.downloadOsm = downloadOsm;
            this.downloadOds = downloadOds;
        }

        @Override
        protected void cancel() {
            downloader.cancel();
        }

        @Override
        protected void realRun() throws SAXException, IOException,
                OsmTransferException {
            downloader = new OdsDownloader(boundary, getProgressMonitor());
            try {
                downloader.run(downloadOsm, downloadOds);
            } catch (ExecutionException|InterruptedException e) {
                throw new OsmTransferException(e);
            }
        }

        @Override
        protected void finish() {
            OdsWorkingSet workingSet = ODS.getModule().getWorkingSet();
            if (downloadOsm) {
                showLayer(workingSet.getInternalDataLayer().getOsmDataLayer());
                
            }
            if (downloadOds) {
                showLayer(workingSet.getExternalDataLayer().getOsmDataLayer());
                
            }
            OsmDataLayer activeLayer = null;
            if (workingSet.getInternalDataLayer() != null && downloadOsm) {
                activeLayer = workingSet.getInternalDataLayer().getOsmDataLayer();
            }
            else if (workingSet.getExternalDataLayer() != null) {
                activeLayer = workingSet.getExternalDataLayer().getOsmDataLayer();
            }
            if (activeLayer != null) {
                Main.map.mapView.setActiveLayer(activeLayer);
            }
        } 
        
        private void showLayer(Layer layer) {
            if (!Main.isDisplayingMapView()) {
                Main.main.addLayer(layer);
            }
            else if (!Main.map.mapView.hasLayer(layer)) {
                Main.map.mapView.addLayer(layer);
            }
        }
    }
}
