package org.openstreetmap.josm.plugins.ods;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.tools.ImageProvider;

public class OdsDownloadAction extends OdsAction {
  /**
     * 
     */
    private static final long serialVersionUID = 1L;

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
    try {
      workingSet.download(area, dialog.isDownloadOsmData());
    } catch (ExecutionException e1) {
      JOptionPane.showMessageDialog(
          Main.parent,
          e1.getMessage(),
          tr("Error during download"),
          JOptionPane.ERROR_MESSAGE);
    } catch (InterruptedException e1) {
      JOptionPane.showMessageDialog(
          Main.parent,
          e1.getMessage(),
          tr("Error during download"),
          JOptionPane.ERROR_MESSAGE);
    }
  }
}
