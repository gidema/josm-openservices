package org.openstreetmap.josm.plugins.ods.io;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.Context;

public interface DownloadJob {
    public List<? extends Downloader> getDownloaders();
    public void process(Context ctx);
    public Status getStatus();
}
