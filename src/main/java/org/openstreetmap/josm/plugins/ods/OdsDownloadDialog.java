package org.openstreetmap.josm.plugins.ods;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.ExpertToggleAction;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.download.DownloadDialog;
import org.openstreetmap.josm.gui.download.SlippyMapChooser;
import org.openstreetmap.josm.tools.GBC;

public class OdsDownloadDialog extends DownloadDialog {
  /** the unique instance of the download dialog */
  static private OdsDownloadDialog instance;

  protected JCheckBox cbDownloadOSM;
  private SlippyMapChooser slippyMapChooser;

  /**
   * Replies the unique instance of the download dialog
   * 
   * @return the unique instance of the download dialog
   */
  static public OdsDownloadDialog getInstance() {
    if (instance == null) {
      instance = new OdsDownloadDialog(Main.parent);
    }
    return instance;
  }

  public OdsDownloadDialog(Component parent) {
    super(parent);
    this.setTitle(tr("Download OpenDataServices"));
  }

  @Override
  public boolean isDownloadOsmData() {
    return cbDownloadOSM.isSelected();
  }

  @Override
  public boolean isDownloadGpxData() {
    // A bit dirty hack to prevent the superclass from complaining about missing download task.
    // No Gpx data will be downloaded.
    return true;
  }

  @Override
  protected JPanel buildMainPanel() {
    JPanel pnl = new JPanel();
    pnl.setLayout(new GridBagLayout());

    cbDownloadOsmData = new JCheckBox(tr("OpenStreetMap data"), true);

    slippyMapChooser = new SlippyMapChooser();
    slippyMapChooser.addGui(this);

    pnl.add(tpDownloadAreaSelectors, GBC.eol().fill());

    Font labelFont = sizeCheck.getFont();
    sizeCheck.setFont(labelFont.deriveFont(Font.PLAIN, labelFont.getSize()));

    // Create dummy checkboxes to prevent error in super class
    cbDownloadGpxData = new JCheckBox();
    cbNewLayer = new JCheckBox();
    cbStartup = new JCheckBox();

    cbDownloadOSM = new JCheckBox(tr("Download OSM data"));
    cbDownloadOSM
        .setToolTipText(tr("<html>Select to download OSM data into a separate data layer.<br>"
            + "Unselect to skip downloading of OSM data.</html>"));
    pnl.add(cbDownloadOSM, GBC.std().anchor(GBC.SOUTHWEST).insets(5, 5, 5, 5));

    pnl.add(sizeCheck,  GBC.eol().anchor(GBC.SOUTHEAST).insets(5,5,5,2));
    
    if (!ExpertToggleAction.isExpert()) {
        JLabel infoLabel  = new JLabel(tr("Use left click&drag to select area, arrows or right mouse button to scroll map, wheel or +/- to zoom."));
        pnl.add(infoLabel,GBC.eol().anchor(GBC.SOUTH).insets(0,0,0,0));
    }
    pnl.revalidate();
    pnl.repaint();
    return pnl;
  }
  
  /* This should not be necessary, but if not here, repaint is not always correct in SlippyMap! */
  @Override
  public void paint(Graphics g) {
      slippyMapChooser.paint(g);
      super.paint(g);
  }

  /**
   * Remembers the current settings in the download dialog
   * 
   */
  @Override
  public void rememberSettings() {
    Main.pref.put("openservices.download.osm", cbDownloadOSM.isSelected());
    if (currentBounds != null) {
      Main.pref.put("openservices.download.bounds", currentBounds.encodeAsString(";"));
    }
  }

  @Override
  public void restoreSettings() {
    cbDownloadOSM.setSelected(Main.pref.getBoolean("openservices.download.osm", true));
    MapView mv = Main.map.mapView;
    if (mv.getX() != 0 || mv.getY() != 0.0) {
      currentBounds = new Bounds(
          mv.getLatLon(0, mv.getHeight()),
          mv.getLatLon(mv.getWidth(), 0)
          );
      slippyMapChooser.setDownloadArea(currentBounds);
    }
    else if (!Main.pref.get("openservices.download.bounds").isEmpty()) {
      // read the bounding box from the preferences
      try {
        currentBounds = new Bounds(Main.pref.get("openservices.download.bounds"), ";");
        slippyMapChooser.setDownloadArea(currentBounds);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
