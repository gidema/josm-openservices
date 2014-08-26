package org.openstreetmap.josm.plugins.ods.io;

import org.openstreetmap.josm.plugins.ods.jts.Boundary;

public interface Downloader {
    void setBoundary(Boundary boundary);

    Status getStatus();

    void prepare() throws InterruptedException;

    void download() throws InterruptedException;

    void process() throws InterruptedException;

    void cancel();
}
