package org.openstreetmap.josm.plugins.ods.io;

public class DownloadResponse {
    private DownloadRequest request;
    private Status status;

    public DownloadResponse(DownloadRequest request) {
        super();
        this.request = request;
    }

    public DownloadRequest getRequest() {
        return request;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
