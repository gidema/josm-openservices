package org.openstreetmap.josm.plugins.ods.io;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.jts.Boundary;

public interface DownloadJob {
    public List<? extends Downloader> getDownloaders();
    public void setBoundary(Boundary boundary);
    public void process();
    public Status getStatus();
}
