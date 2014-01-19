package org.openstreetmap.josm.plugins.ods;

import java.util.concurrent.Callable;

import org.openstreetmap.josm.gui.progress.ProgressMonitor.CancelListener;

public interface DownloadTask extends CancelListener {
    public Callable<Object> getPrepareCallable();
    public Callable<?> getDownloadCallable();
    boolean cancelled();
    boolean failed();
    Exception getException();
}
