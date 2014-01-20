package org.openstreetmap.josm.plugins.ods;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
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
            DownloadTask task = new DownloadTask(boundary);
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
        if (boundary != null) {
            return boundary;
        }
        OdsDownloadDialog dialog = OdsDownloadDialog.getInstance();
        dialog.restoreSettings();
        dialog.setVisible(true);
        if (dialog.isCanceled()) {
            cancelled = true;
            return null;
        }
        dialog.rememberSettings();
        Bounds bounds = dialog.getSelectedDownloadArea();
        return new Boundary(bounds);
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
        if (primitive.getDisplayType() != OsmPrimitiveType.CLOSEDWAY) {
            return null;
        }
        return new Boundary((Way)primitive);
    }
    
    private class DownloadTask extends PleaseWaitRunnable {
        private boolean cancelled = false;
        private Boundary boundary;
        
        public DownloadTask(Boundary boundary) {
            super(tr("Downloading data"));
            this.boundary = boundary;
        }

        @Override
        protected void cancel() {
            this.cancelled = true;
        }

        @Override
        protected void realRun() throws SAXException, IOException,
                OsmTransferException {
            OdsDownloader downloader = new OdsDownloader(boundary, getProgressMonitor());
            try {
                downloader.run();
            } catch (ExecutionException|InterruptedException e) {
                throw new OsmTransferException(e);
            }
        }

        @Override
        protected void finish() {
            // TODO Auto-generated method stub
        }
        
    }
}
