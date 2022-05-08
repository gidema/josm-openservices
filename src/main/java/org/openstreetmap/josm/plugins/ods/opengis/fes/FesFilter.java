package org.openstreetmap.josm.plugins.ods.opengis.fes;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.openstreetmap.josm.plugins.ods.http.OdsHttpClient;
import org.openstreetmap.josm.plugins.ods.wfs.query.OdsQueryFilter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FesFilter implements OdsQueryFilter {
    public static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private final FilterPredicate predicate;
    
    public FesFilter(FilterPredicate predicate) {
        super();
        this.predicate = predicate;
    }

    @Override
    public String getQueryParameterName() {
        return "filter";
    }

    @Override
    public String toString() {
        factory.setNamespaceAware(false);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element filterElement = buildXmlElement(doc);
            return OdsHttpClient.getXmlString(filterElement, false, true);
        } catch (ParserConfigurationException | TransformerException e) {
             throw new RuntimeException(e);
        }
    }
    
    @Override
    public Element buildXmlElement(Document doc) {
        Element filter = doc.createElement("fes:Filter");
        filter.appendChild(predicate.buildXmlElement(doc));
        return filter;
    }
}
