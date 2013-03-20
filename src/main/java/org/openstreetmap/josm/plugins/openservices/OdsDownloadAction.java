package org.openstreetmap.josm.plugins.openservices;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.tools.ImageProvider;

public class OdsDownloadAction extends AbstractAction {
  private final boolean enabled = true;
  private final OdsWorkingSet workingSet;

  public OdsDownloadAction(OdsWorkingSet workingSet) {
    super("", ImageProvider.get("download"));
    this.workingSet = workingSet;
    //super(name, iconName, toolTip, null, false, toolbarId, false);
    //putValue("help", helpTopic);
  }

  public void setName(String name) {
    this.putValue(Action.NAME, name);
    this.putValue("toolbar", name);
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
    
//    Layer activeLayer = null;
//    if (Main.isDisplayingMapView()) {
//      activeLayer = Main.map.mapView.getActiveLayer();
//    }
//    Future<?> future1 = null;
//    DownloadOsmTask task;
//    if (dialog.isDownloadOsmData()) {
//      activateOsmLayer();
//      String osmQuery = workingSet.getOsmQuery();
//      if (osmQuery != null) {
//        String url = getOverpassUrl(osmQuery, area);
//        task = new DownloadOsmTask();
//        future1 = task.loadUrl(dialog.isNewLayerRequired(), url, null);
//      }
//      else {
//        task = new DownloadOsmTask();
//        future1 = task.download(dialog.isNewLayerRequired(), area, null);
//      }
//      Main.worker.submit(new org.openstreetmap.josm.actions.downloadtasks.PostDownloadHandler(task, future1));
//    }
//    for (OdsDataSource dataSource : workingSet.getDataSources().values()) {
//      OdsDownloadTask downloadTask = dataSource.getDownloadTask();
//      Future<?> future2 = downloadTask.download(false, area, null);
//      Main.worker.submit(new PostDownloadHandler(future2));
//    }
  }
}
