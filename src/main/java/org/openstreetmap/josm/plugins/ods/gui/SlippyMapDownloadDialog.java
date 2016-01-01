package org.openstreetmap.josm.plugins.ods.gui;

import static org.openstreetmap.josm.tools.I18n.tr;

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
import org.openstreetmap.josm.data.ProjectionBounds;
import org.openstreetmap.josm.data.projection.Projection;
import org.openstreetmap.josm.gui.bbox.BBoxChooser;
import org.openstreetmap.josm.gui.bbox.SlippyMapBBoxChooser;
import org.openstreetmap.josm.plugins.ods.OdsModule;
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

    protected Bounds currentBounds = null;
    
    public SlippyMapDownloadDialog(OdsModule module) {
        super(module, tr("Download ODS"));
    }

    protected SlippyMapBBoxChooser slippyMap;

    protected JPanel buildMainPanel() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new GridBagLayout());

        String moduleName = module.getName();
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
            Bounds bounds = eastNorthToLatLon(Main.map.mapView.getProjectionBounds(), Main.map.mapView.getProjection());
            if (bounds.getCenter().getX() != 0.0 || bounds.getCenter().getY() != 0.0) {
                return bounds;
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

    private Bounds eastNorthToLatLon(ProjectionBounds bounds, Projection proj) {
        return new Bounds(proj.eastNorth2latlon(bounds.getMin()),
            proj.eastNorth2latlon(bounds.getMax()));
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
}
