package org.openstreetmap.josm.plugins.ods.io;

import org.openstreetmap.josm.gui.progress.ProgressMonitor;

public interface MainDownloader {

    public void cancel();

    public void run(ProgressMonitor progressMonitor);

}
