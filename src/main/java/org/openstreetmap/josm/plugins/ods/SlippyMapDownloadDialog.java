package org.openstreetmap.josm.plugins.ods;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.ExpertToggleAction;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.bbox.BBoxChooser;
import org.openstreetmap.josm.gui.bbox.SlippyMapBBoxChooser;
import org.openstreetmap.josm.tools.GBC;

/**
 * Dialog box to download a polygon area.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class SlippyMapDownloadDialog extends AbstractDownloadDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static SlippyMapDownloadDialog instance = null;
    protected Bounds currentBounds = null;
    
    /**
     * Replies the unique instance of this download dialog
     * 
     * @return the unique instance of this download dialog
     */
    static public SlippyMapDownloadDialog getInstance() {
        if (instance == null) {
                instance = new SlippyMapDownloadDialog(Main.parent);
        }
        return instance;
    }

    protected SlippyMapBBoxChooser slippyMap;

    public SlippyMapDownloadDialog(Component parent) {
        super(parent);
    }

    protected JPanel buildMainPanel() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new GridBagLayout());

        String moduleName = ODS.getModule().getName();
        cbDownloadOSM = new JCheckBox(tr("Download OSM data"));
        cbDownloadOSM.setToolTipText(tr("<html>Select to download OSM data.<br>"
                        + "Unselect to skip downloading of OSM data.</html>"));
        cbDownloadODS = new JCheckBox(tr("Download {0} data", moduleName));
        cbDownloadODS.setToolTipText(tr("<html>Select to download {0}.<br>"
                        + "Unselect to skip downloading of {0} data.</html>", moduleName));

        slippyMap = new SlippyMapBBoxChooser();
        slippyMap.addPropertyChangeListener(this);
        pnl.add(slippyMap, GBC.eol().fill());

        pnl.add(cbDownloadOSM,
                GBC.std().anchor(GBC.SOUTHWEST).insets(5, 5, 5, 5));
        pnl.add(cbDownloadODS,
                GBC.eol().anchor(GBC.SOUTHWEST).insets(5, 5, 5, 5));

//        pnl.add(sizeCheck, GBC.eol().anchor(GBC.SOUTHEAST).insets(5, 5, 5, 2));

        if (!ExpertToggleAction.isExpert()) {
            JLabel infoLabel = new JLabel(
                    tr("Use left click&drag to select area, arrows or right mouse button to scroll map, wheel or +/- to zoom."));
            pnl.add(infoLabel, GBC.eol().anchor(GBC.SOUTH).insets(0, 0, 0, 0));
        }
        pnl.revalidate();
        pnl.repaint();
        return pnl;
    }

    
    @Override
    public void rememberSettings() {
        super.rememberSettings();
        if (currentBounds != null) {
            Main.pref.put("openservices.download.bounds",
                    currentBounds.encodeAsString(";"));
        }
    }

    
    @Override
    public void restoreSettings() {
        super.restoreSettings();
        Bounds bbox = createBBox();
        if (bbox != null) {
            boundingBoxChanged(bbox);
        }
    }

    private void boundingBoxChanged(Bounds bbox) {
        currentBounds = bbox;
        if (slippyMap != null) {
            slippyMap.setBoundingBox(bbox);
            slippyMap.repaint();
        }
    }
        
    public Bounds getSelectedDownloadArea() {
        return currentBounds;
    }

    /* This should not be necessary, but if not here, repaint is not always correct in SlippyMap! */
    @Override
    public void paint(Graphics g) {
        if (slippyMap != null) {
            slippyMap.paint(g);
        }
        super.paint(g);
    }

    /**
     * Create a default bounding box for the download. If a mapView
     * is showing, create the bounding box from the visible area.
     * If no mapView is showing, restore the bounding box from the
     * saved bounding box. If its missing, return null
     * @return
     */
    private Bounds createBBox() {
        if (Main.isDisplayingMapView()) {
            MapView mv = Main.map.mapView;
            if (mv.getX() != 0 || mv.getY() != 0.0) {
                return new Bounds(mv.getLatLon(0, mv.getHeight()),
                    mv.getLatLon(mv.getWidth(), 0));
            }
        }
        if (!Main.pref.get("openservices.download.bounds").isEmpty()) {
            // read the bounding box from the preferences
            try {
                return new Bounds(
                    Main.pref.get("openservices.download.bounds"), ";");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected Dimension getDimension() {
        return new Dimension(1000, 600);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(BBoxChooser.BBOX_PROP)) {
            currentBounds = (Bounds)evt.getNewValue();
        }
    }

//    /** the unique instance of the download dialog */
//    static private FixedBoundsDownloadDialog instance;
//    protected Bounds currentBounds = null;
//    protected boolean canceled;
//
//    protected JCheckBox cbDownloadOSM;
//    protected JCheckBox cbDownloadODS;
//    /** the download action and button */
//    private DownloadAction actDownload;
//    protected SideButton btnDownload;
//
//    /**
//     * Replies the unique instance of the download dialog
//     * 
//     * @return the unique instance of the download dialog
//     */
//    static public FixedBoundsDownloadDialog getInstance() {
//        if (instance == null) {
//            instance = new FixedBoundsDownloadDialog(Main.parent);
//        }
//        return instance;
//    }
//
//    public FixedBoundsDownloadDialog(Component parent) {
//        super(JOptionPane.getFrameForComponent(parent),tr("Download ODS with polygon"), ModalityType.DOCUMENT_MODAL);
//        getContentPane().setLayout(new BorderLayout());
//        getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
//        getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
//    }
//
//    public boolean isDownloadOsmData() {
//        return cbDownloadOSM.isSelected();
//    }
//
//    public boolean isDownloadOdsData() {
//        return cbDownloadODS.isSelected();
//    }
//
//    protected JPanel buildMainPanel() {
//        JPanel pnl = new JPanel();
//        pnl.setLayout(new GridBagLayout());
//
//        String moduleName = ODS.getModule().getName();
//        cbDownloadOSM = new JCheckBox(tr("Download OSM data"));
//        cbDownloadOSM
//                .setToolTipText(tr("<html>Select to download OSM data.<br>"
//                        + "Unselect to skip downloading of OSM data.</html>"));
//        cbDownloadODS = new JCheckBox(tr("Download {0} data", moduleName));
//        cbDownloadODS
//                .setToolTipText(tr("<html>Select to download {0}.<br>"
//                        + "Unselect to skip downloading of {0} data.</html>", moduleName));
//        pnl.add(cbDownloadOSM,
//                GBC.std().anchor(GBC.SOUTHWEST).insets(5, 5, 5, 5));
//        pnl.add(cbDownloadODS,
//                GBC.eol().anchor(GBC.SOUTHWEST).insets(5, 5, 5, 5));
//
////        pnl.add(sizeCheck, GBC.eol().anchor(GBC.SOUTHEAST).insets(5, 5, 5, 2));
//
//        if (!ExpertToggleAction.isExpert()) {
//            JLabel infoLabel = new JLabel(
//                    tr("Use left click&drag to select area, arrows or right mouse button to scroll map, wheel or +/- to zoom."));
//            pnl.add(infoLabel, GBC.eol().anchor(GBC.SOUTH).insets(0, 0, 0, 0));
//        }
//        pnl.revalidate();
//        pnl.repaint();
//        return pnl;
//    }
//
//    protected JPanel buildButtonPanel() {
//        JPanel pnl = new JPanel();
//        pnl.setLayout(new FlowLayout());
//
//        // -- download button
//        pnl.add(btnDownload = new SideButton(actDownload = new DownloadAction()));
//        InputMapUtils.enableEnter(btnDownload);
//
//        // -- cancel button
//        SideButton btnCancel;
//        CancelAction actCancel = new CancelAction();
//        pnl.add(btnCancel = new SideButton(actCancel));
//        InputMapUtils.enableEnter(btnCancel);
//
//        // -- cancel on ESC
//        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0), "cancel");
//        getRootPane().getActionMap().put("cancel", actCancel);
//
//        // -- help button
//        SideButton btnHelp;
//        pnl.add(btnHelp = new SideButton(new ContextSensitiveHelpAction(ht("/Action/Download"))));
//        InputMapUtils.enableEnter(btnHelp);
//
//        return pnl;
//    }
//
//    /**
//     * Remembers the current settings in the download dialog
//     * 
//     */
//    public void rememberSettings() {
//        Main.pref.put("openservices.download.osm", cbDownloadOSM.isSelected());
//        Main.pref.put("openservices.download.ods", cbDownloadODS.isSelected());
//        if (currentBounds != null) {
//            Main.pref.put("openservices.download.bounds",
//                    currentBounds.encodeAsString(";"));
//        }
//    }
//
//    public void restoreSettings() {
//        cbDownloadOSM.setSelected(Main.pref.getBoolean(
//                "openservices.download.osm", true));
//        cbDownloadODS.setSelected(Main.pref.getBoolean(
//                "openservices.download.ods", true));
//        currentBounds = createBBox();
//    }
//    
//    /**
//     * Create a default bounding box for the download. If a mapView
//     * is showing, create the bounding box from the visible area.
//     * If no mapView is showing, restore the bounding box from the
//     * saved bounding box. If its missing, return null
//     * @return
//     */
//    private Bounds createBBox() {
//        if (Main.isDisplayingMapView()) {
//            MapView mv = Main.map.mapView;
//            if (mv.getX() != 0 || mv.getY() != 0.0) {
//                return new Bounds(mv.getLatLon(0, mv.getHeight()),
//                    mv.getLatLon(mv.getWidth(), 0));
//            }
//        }
//        if (!Main.pref.get("openservices.download.bounds").isEmpty()) {
//            // read the bounding box from the preferences
//            try {
//                return new Bounds(
//                    Main.pref.get("openservices.download.bounds"), ";");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }
//    
//    @Override
//    public void setVisible(boolean visible) {
//        if (visible) {
//            new WindowGeometry(
//                    getClass().getName() + ".geometry",
//                    WindowGeometry.centerInWindow(
//                            getParent(),
//                            new Dimension(300, 200)
//                    )
//            ).applySafe(this);
//        } else if (isShowing()) { // Avoid IllegalComponentStateException like in #8775
//            new WindowGeometry(this).remember(getClass().getName() + ".geometry");
//        }
//        super.setVisible(visible);
//    }
//
//    /**
//     * Replies true if the dialog was canceled
//     *
//     * @return true if the dialog was canceled
//     */
//    public boolean isCanceled() {
//        return canceled;
//    }
//
//    protected void setCanceled(boolean canceled) {
//        this.canceled = canceled;
//    }
//
//    class CancelAction extends AbstractAction {
//        
//        private static final long serialVersionUID = 1L;
//
//        public CancelAction() {
//            putValue(NAME, tr("Cancel"));
//            putValue(SMALL_ICON, ImageProvider.get("cancel"));
//            putValue(SHORT_DESCRIPTION, tr("Click to close the dialog and to abort downloading"));
//        }
//
//        public void run() {
//            setCanceled(true);
//            setVisible(false);
//        }
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            run();
//        }
//    }
//
//    class DownloadAction extends AbstractAction {
//        /**
//         * 
//         */
//        private static final long serialVersionUID = 1L;
//
//        public DownloadAction() {
//            putValue(NAME, tr("Download"));
//            putValue(SMALL_ICON, ImageProvider.get("download"));
//            putValue(SHORT_DESCRIPTION, tr("Click to download the currently selected area"));
//        }
//
//        public void run() {
//            if (currentBounds == null) {
//                JOptionPane.showMessageDialog(
//                        FixedBoundsDownloadDialog.this,
//                        tr("Please select a download area first."),
//                        tr("Error"),
//                        JOptionPane.ERROR_MESSAGE
//                );
//                return;
//            }
//            if (!isDownloadOsmData() && !isDownloadOdsData()) {
//                JOptionPane.showMessageDialog(
//                        FixedBoundsDownloadDialog.this,
//                        tr("<html>Neither <strong>{0}</strong> nor <strong>{1}</strong> is enabled.<br>"
//                                + "Please choose to either download OSM data, or {2} data, or both.</html>",
//                                cbDownloadOSM.getText(),
//                                cbDownloadODS.getText(),
//                                ODS.getModule().getName()
//                        ),
//                        tr("Error"),
//                        JOptionPane.ERROR_MESSAGE
//                );
//                return;
//            }
//            setCanceled(false);
//            setVisible(false);
//        }
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            run();
//        }
//    }
//
//    class WindowEventHandler extends WindowAdapter {
//        @Override
//        public void windowClosing(WindowEvent e) {
//            new CancelAction().run();
//        }
//
//        @Override
//        public void windowActivated(WindowEvent e) {
//            btnDownload.requestFocusInWindow();
//        }
//    }
}
