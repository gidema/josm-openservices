package org.openstreetmap.josm.plugins.ods.io;

import java.time.LocalDateTime;

import org.openstreetmap.josm.plugins.ods.jts.Boundary;

public class DownloadRequest {
    private LocalDateTime downloadTime; // Start of the download
    private Boundary boundary; // Boundary for the request
    private int timeout; // timeout in milliseconds
    private boolean getOsm; // true if downloading of Osm data within the
                            // boundary is required
    private boolean getOds; // true if downloading of Open Data within the
                            // boundary is required

    public DownloadRequest(LocalDateTime downloadTime, Boundary boundary,
            boolean getOsm, boolean getOds) {
        super();
        this.downloadTime = downloadTime;
        this.boundary = boundary;
        this.getOsm = getOsm;
        this.getOds = getOds;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
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
