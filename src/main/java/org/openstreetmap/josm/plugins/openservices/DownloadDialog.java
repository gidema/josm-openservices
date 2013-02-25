package org.openstreetmap.josm.plugins.openservices;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.tools.GBC;

public class DownloadDialog extends org.openstreetmap.josm.gui.download.DownloadDialog {
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
  protected JPanel buildMainPanel() {
    JPanel pnl = super.buildMainPanel();
    pnl.remove(this.cbNewLayer);
    pnl.remove(this.cbStartup);
    cbDownloadOSM = new JCheckBox(tr("Download OSM data"));
    cbDownloadOSM.setToolTipText(tr("<html>Select to download OSM data into a separate data layer.<br>"
            +"Unselect to skip downloading of OSM data.</html>"));
    pnl.add(cbDownloadOSM, GBC.std().anchor(GBC.WEST).insets(5,5,5,5));
    return pnl;
  }
}
