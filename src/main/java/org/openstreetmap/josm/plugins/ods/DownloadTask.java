package org.openstreetmap.josm.plugins.ods;

import java.util.concurrent.Callable;

public interface DownloadTask {
    public Callable<Object> stage(String subTask);

    public void cancel();
    public boolean cancelled();
    public boolean failed();
    public String getMessage();

    //Exception getException();
}
