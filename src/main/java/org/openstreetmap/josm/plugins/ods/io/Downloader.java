package org.openstreetmap.josm.plugins.ods.io;

// TODO consider changing the method signatures from Runnable to Callable
// returning a Status object
public interface Downloader {
    public void setup(DownloadRequest request);
    
    public void prepare();

    public void download();

    public void process();

    public void cancel();

    public Status getStatus();
}
