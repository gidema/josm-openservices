package org.openstreetmap.josm.plugins.ods.wfs.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WfsQuery {

    private String xmlns = "http://www.opengis.net/wfs/2.0";

    private final QName featureName;
    private final OdsQueryFilter filter;
    private final Long srid;
    private final List<String> selectedProperties;

    public WfsQuery(QName featureName, OdsQueryFilter filter, Long srid, List<String> selectedProperties) {
        super();
        this.featureName = featureName;
        this.filter = filter;
        this.srid = srid;
        this.selectedProperties = selectedProperties;
    }

    public Element buildXmlElement(Document doc) {
        Element query = doc.createElementNS(xmlns, "Query");
        query.setAttribute("typeNames", featureName.getLocalPart());
        query.setAttribute("xmlns:" + featureName.getPrefix(), featureName.getNamespaceURI());
        if (srid != null) {
            query.setAttribute("srsName", "EPSG:" + srid.toString());
        }
        if (selectedProperties != null && selectedProperties.size() > 0) {
            query.setAttribute("propertyName", String.join(",", selectedProperties));
        }
        if (filter != null) {
            query.appendChild(filter.buildXmlElement(doc));
        }
        return query;
    }

    public Map<String, String> buildQueryParameters() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("typeName", featureName.getLocalPart());
        if (srid != null) {
            parameters.put("srsName", "EPSG:" + srid.toString());
        }
        if (selectedProperties != null && selectedProperties.size() > 0) {
            parameters.put("propertyName", String.join(",", selectedProperties));
        }
        if (filter != null) {
             parameters.put(filter.getQueryParameterName(), filter.toString());
        }
        if (selectedProperties != null && selectedProperties.size() > 0) {
            parameters.put("propertyName", String.join(",", selectedProperties));
        }
        return parameters;
    }
    
    public QName getFeatureName() {
        return featureName;
    }

    public OdsQueryFilter getFilter() {
        return filter;
    }

    public List<String> getSelectedProperties() {
        return selectedProperties;
    }
    
}
