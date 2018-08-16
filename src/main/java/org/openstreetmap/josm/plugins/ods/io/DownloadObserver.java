package org.openstreetmap.josm.plugins.ods.io;

public interface DownloadObserver {
    public void downloadFinished(DownloadResponse responce);
}
