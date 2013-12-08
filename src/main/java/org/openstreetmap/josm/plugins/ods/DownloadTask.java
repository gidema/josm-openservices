package org.openstreetmap.josm.plugins.ods;

import java.util.concurrent.Callable;

public interface DownloadTask {
    public Callable<?> getPrepareCallable();
    public Callable<?> getDownloadCallable();
}
