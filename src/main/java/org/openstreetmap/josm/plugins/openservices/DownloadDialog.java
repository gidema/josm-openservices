package org.openstreetmap.josm.plugins.openservices;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.tools.GBC;

public class DownloadDialog extends
    org.openstreetmap.josm.gui.download.DownloadDialog {
  /** the unique instance of the download dialog */
  static private DownloadDialog instance;

  protected JCheckBox cbDownloadOSM;

  /**
   * Replies the unique instance of the download dialog
   * 
   * @return the unique instance of the download dialog
   */
  static public DownloadDialog getInstance() {
    if (instance == null) {
      instance = new DownloadDialog(Main.parent);
    }
    return instance;
  }

  public DownloadDialog(Component parent) {
    super(parent);
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
    JPanel pnl = super.buildMainPanel();
    pnl.remove(this.cbDownloadOsmData);
    pnl.remove(this.cbDownloadGpxData);
    pnl.remove(this.cbNewLayer);
    pnl.remove(this.cbStartup);
    cbDownloadOSM = new JCheckBox(tr("Download OSM data"));
    cbDownloadOSM
        .setToolTipText(tr("<html>Select to download OSM data into a separate data layer.<br>"
            + "Unselect to skip downloading of OSM data.</html>"));
    pnl.add(cbDownloadOSM, GBC.std().anchor(GBC.WEST).insets(5, 5, 5, 5));
    return pnl;
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
    if (Main.isDisplayingMapView()) {
      MapView mv = Main.map.mapView;
      currentBounds = new Bounds(
          mv.getLatLon(0, mv.getHeight()),
          mv.getLatLon(mv.getWidth(), 0)
          );
      boundingBoxChanged(currentBounds, null);
    }
    else if (!Main.pref.get("openservices.download.bounds").isEmpty()) {
      // read the bounding box from the preferences
      try {
        currentBounds = new Bounds(Main.pref.get("openservices.download.bounds"), ";");
        boundingBoxChanged(currentBounds, null);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
