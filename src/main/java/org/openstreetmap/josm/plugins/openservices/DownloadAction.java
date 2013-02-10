package org.openstreetmap.josm.plugins.openservices;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.util.LinkedHashSet;
import java.util.concurrent.Future;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.downloadtasks.DownloadTask;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.gui.download.DownloadDialog;

public class DownloadAction extends AbstractAction {
  private final boolean enabled = true;
  private Layer layer;

  public DownloadAction() {
    super();
    //super(name, iconName, toolTip, null, false, toolbarId, false);
    //putValue("help", helpTopic);
  }

  public void setName(String name) {
    this.
    putValue(Action.NAME, name);
  }
  
  public final void setLayer(Layer layer) {
    this.layer = layer;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    DownloadDialog dialog = DownloadDialog.getInstance();
    dialog.restoreSettings();
    dialog.setVisible(true);
    if (!dialog.isCanceled()) {
      dialog.rememberSettings();
      Bounds area = dialog.getSelectedDownloadArea();
      DataSource dataSource = layer.getDataSource();
      dataSource.download(area);
    }
  }

//  @Override
//  public void actionPerformed(ActionEvent e) {
//    DownloadDialog dialog = DownloadDialog.getInstance();
//    dialog.restoreSettings();
//    dialog.setVisible(true);
//    if (!dialog.isCanceled()) {
//      dialog.rememberSettings();
//      Bounds area = dialog.getSelectedDownloadArea();
//      DownloadTask task = getDownloadTask();
//      Future<?> future = task.download(true, area, null);
//      Main.worker.submit(new PostDownloadHandler(task, future));
//    }
//  }
//  
  protected DownloadTask getDownloadTask() {
    return null;
  }

  class PostDownloadHandler implements Runnable {
    private final DownloadTask task;
    private final Future<?> future;

    PostDownloadHandler(DownloadTask task, Future<?> future) {
      super();
      this.task = task;
      this.future = future;
    }

    @Override
    public void run() {
      try {
        future.get();
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }
      // make sure errors are reported only once
      //
      LinkedHashSet<Object> errors = new LinkedHashSet<Object>();
      errors.addAll(task.getErrorObjects());
      if (errors.isEmpty())
        return;

      // just one error object?
      //
      if (errors.size() == 1) {
        final Object error = errors.iterator().next();
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            JOptionPane.showMessageDialog(
                Main.parent,
                error.toString(),
                tr("Error during download"),
                JOptionPane.ERROR_MESSAGE);

          }
        });
        return;
      }

    }
  }
}