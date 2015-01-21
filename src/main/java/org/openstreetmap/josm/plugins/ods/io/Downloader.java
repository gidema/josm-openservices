package org.openstreetmap.josm.plugins.ods.io;

import org.openstreetmap.josm.plugins.ods.Context;

public interface Downloader {
    Status getStatus();

    void prepare(Context ctx) throws InterruptedException;

    void download() throws InterruptedException;

    void process() throws InterruptedException;

    void cancel();
}
