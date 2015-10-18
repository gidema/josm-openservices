package org.openstreetmap.josm.plugins.ods.io;

// TODO rename to LayerDownloader and comment
public interface LayerDownloader {
    public void setup(DownloadRequest request);
    public void prepare();
    public void download();
    public void process();
    public void cancel();
    public Status getStatus();
}
