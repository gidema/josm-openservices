package org.openstreetmap.josm.plugins.openservices;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.ExceptionDialogUtil;

public class PostDownloadHandler implements Runnable {
  private List<Future<?>> futures;

  public PostDownloadHandler(Future<?> future) {
    this.futures = new ArrayList<Future<?>>();
    if (future != null) {
      this.futures.add(future);
    }
  }

  public PostDownloadHandler(List<Future<?>> futures) {
    this.futures = new ArrayList<Future<?>>();
    if (futures == null)
      return;
    for (Future<?> future : futures) {
      this.futures.add(future);
    }
  }

  @Override
  public void run() {
    // wait for all downloads task to finish (by waiting for the futures
    // to return a value)
    //
    for (Future<?> future : futures) {
      try {
        future.get();
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }
    }

    // make sure errors are reported only once
    //
    LinkedHashSet<Object> errors = new LinkedHashSet<Object>();
    // errors.addAll(task.getErrorObjects());
    if (errors.isEmpty())
      return;

    // just one error object?
    //
    if (errors.size() == 1) {
      final Object error = errors.iterator().next();
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          if (error instanceof Exception) {
            ExceptionDialogUtil.explainException((Exception) error);
          } else {
            JOptionPane.showMessageDialog(
                Main.parent,
                error.toString(),
                tr("Error during download"),
                JOptionPane.ERROR_MESSAGE);
          }
        }
      });
      return;
    }
  };

}
