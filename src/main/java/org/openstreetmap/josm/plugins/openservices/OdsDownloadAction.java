package org.openstreetmap.josm.plugins.openservices;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.data.Bounds;

public class OdsDownloadAction extends OdsAction {
  public OdsDownloadAction() {
    super();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    OdsDownloadDialog dialog = OdsDownloadDialog.getInstance();
    dialog.restoreSettings();
    dialog.setVisible(true);
    if (dialog.isCanceled()) {
      return;
    }
    dialog.rememberSettings();
    Bounds area = dialog.getSelectedDownloadArea();
    workingSet.download(area, dialog.isDownloadOsmData());
  }
}
