package org.openstreetmap.josm.plugins.ods.io;

import java.util.List;

public interface DownloadJob {
    public List<? extends Downloader> getDownloaders();
}
