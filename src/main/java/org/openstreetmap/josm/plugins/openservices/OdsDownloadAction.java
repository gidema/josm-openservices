package org.openstreetmap.josm.plugins.openservices;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.tools.ImageProvider;

public class OdsDownloadAction extends OdsAction {
  public OdsDownloadAction() {
    super();
    this.setName("Download");
    this.setIcon(ImageProvider.get("download"));
    
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
