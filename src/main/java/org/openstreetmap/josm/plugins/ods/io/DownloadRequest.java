package org.openstreetmap.josm.plugins.ods.io;

import java.util.Date;

import org.openstreetmap.josm.plugins.ods.jts.Boundary;

public class DownloadRequest {
    private Date downloadTime;
    private Boundary boundary;
    private boolean getOsm;
    private boolean getOds;

    public DownloadRequest(Date downloadTime, Boundary boundary,boolean getOsm, boolean getOds) {
        super();
        this.downloadTime = downloadTime;
        this.boundary = boundary;
        this.getOsm = getOsm;
        this.getOds = getOds;
    }

    public Date getDownloadTime() {
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
