package org.openstreetmap.josm.plugins.ods.io;

import exceptions.OdsException;

/**
 * Marker interface
 */
public interface LayerDownloader extends Downloader {
    public abstract void initialize() throws OdsException;

    public void setResponse(DownloadResponse response);
}
