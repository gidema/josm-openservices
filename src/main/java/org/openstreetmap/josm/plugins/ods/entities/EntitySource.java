package org.openstreetmap.josm.plugins.ods.entities;

import java.util.Date;

import org.openstreetmap.josm.plugins.ods.jts.Boundary;

public class EntitySource {
    private Date downloadTime;
    private Boundary boundary;

    public EntitySource(Date downloadTime, Boundary boundary) {
        super();
        this.downloadTime = downloadTime;
        this.boundary = boundary;
    }

    public Date getDownloadTime() {
        return downloadTime;
    }

    public Boundary getBoundary() {
        return boundary;
    }
}
