package org.openstreetmap.josm.plugins.ods.wfs.query;

public class GetFeatureRequest {
    private String url;
    private WfsQuery wfsQuery;
    private Integer startIndex;
    private Integer pageSize;

    public GetFeatureRequest(String url, WfsQuery wfsQuery, Integer startIndex,
            Integer pageSize) {
        super();
        this.url = url;
        this.wfsQuery = wfsQuery;
        this.startIndex = startIndex;
        this.pageSize = pageSize;
    }

    public GetFeatureRequest(GetFeatureRequest request) {
        this(request.url, request.wfsQuery, request.startIndex, request.pageSize);
    }
    
    public String getUrl() {
        return url;
    }

    public WfsQuery getWfsQuery() {
        return wfsQuery;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }
    
}
