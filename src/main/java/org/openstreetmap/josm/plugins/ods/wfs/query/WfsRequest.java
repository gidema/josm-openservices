package org.openstreetmap.josm.plugins.ods.wfs.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.openstreetmap.josm.plugins.ods.http.OdsHttpClient;
import org.openstreetmap.josm.plugins.ods.opengis.fes.Fes;
import org.openstreetmap.josm.plugins.ods.wfs.Wfs;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WfsRequest {
    private final String url;
    private final WfsQuery wfsQuery;
    private final Integer startIndex;
    private final Integer pageSize;
    private final List<String> sortBy;

    public WfsRequest(String url, WfsQuery wfsQuery, Integer startIndex,
            Integer pageSize, List<String> sortBy) {
        super();
        this.url = url;
        this.wfsQuery = wfsQuery;
        this.startIndex = startIndex;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
    }

    public WfsRequest(WfsRequest request) {
        this(request.url, request.wfsQuery, request.startIndex, request.pageSize, request.sortBy);
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
    
    private Document buildPostDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElementNS(Wfs.NS_WFS_200, "GetFeature");
            root.setAttribute("xmlns:fes", Fes.NS_FES_20);
            doc.appendChild(root);
            root.setAttribute("service", "WFS");
            root.setAttribute("version", "2.0.0");
            if (pageSize != null) root.setAttribute("count", pageSize.toString());
            if (startIndex != null) root.setAttribute("startIndex", startIndex.toString());
            if (sortBy != null) root.setAttribute("sortBy", String.join(",", sortBy));
            root.appendChild(wfsQuery.buildXmlElement(doc));
            return doc;
        } catch (ParserConfigurationException e) {
             throw new RuntimeException(e);
        }
    }
    
    public String getPostData() {
        try {
            return OdsHttpClient.getXmlString(buildPostDocument(), true, false);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> getQueryParameters() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("request", "GetFeature");
        parameters.put("service", "WFS");
        parameters.put("version", "2.0.0");
        if (pageSize != null) parameters.put("count", pageSize.toString());
        if (startIndex != null) parameters.put("startIndex", startIndex.toString());
        if (sortBy != null) parameters.put("sortBy", String.join(",", sortBy));
        parameters.putAll(wfsQuery.buildQueryParameters());
        return parameters;
    }
}
