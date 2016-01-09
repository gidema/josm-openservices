package org.openstreetmap.josm.plugins.ods.io;

import java.time.LocalDateTime;

import org.openstreetmap.josm.plugins.ods.jts.Boundary;

public class DownloadRequest {
    private LocalDateTime downloadTime;
    private Boundary boundary;
    private boolean getOsm;
    private boolean getOds;

    public DownloadRequest(LocalDateTime downloadTime, Boundary boundary,boolean getOsm, boolean getOds) {
        super();
        this.downloadTime = downloadTime;
        this.boundary = boundary;
        this.getOsm = getOsm;
        this.getOds = getOds;
    }

    public LocalDateTime getDownloadTime() {
        return downloadTime;
    }

    public Boundary getBoundary() {
        return boundary;
    }

    public boolean isGetOsm() {
        return getOsm;
    }

    public boolean isGetOds() {
        return getOds;
    }
}
