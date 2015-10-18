package org.openstreetmap.josm.plugins.ods.entities.external;

import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;

public interface FeatureDownloader {
    public void setup(DownloadRequest request);
    public void prepare();
    public void download();
    public void process();
    public void cancel();
}
