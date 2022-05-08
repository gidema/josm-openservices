package org.openstreetmap.josm.plugins.ods.io;

import java.time.LocalDateTime;

import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;

public class DownloadRequest {
    private LocalDateTime downloadTime;
    private Boundary boundary;

    public DownloadRequest(LocalDateTime downloadTime, Boundary boundary) {
        super();
        this.downloadTime = downloadTime;
        this.boundary = boundary;
    }

    public LocalDateTime getDownloadTime() {
        return downloadTime;
    }

    public Boundary getBoundary() {
        return boundary;
    }

    /**
     * Transform the request boundary to the Coordinate reference system specified by the srid
     *      * 
     * @param crsUtil The CRSUtil instance to use for the transformation
     * @param srid The (EPSG) srid
     * @return
     */
    public DownloadRequest transform(CRSUtil crsUtil, Long srid) {
        Boundary transformedBoundary  = boundary.transform(crsUtil, srid);
        return new DownloadRequest(downloadTime, transformedBoundary);
    }
}
