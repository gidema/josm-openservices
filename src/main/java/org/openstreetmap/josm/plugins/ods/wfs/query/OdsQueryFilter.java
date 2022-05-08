package org.openstreetmap.josm.plugins.ods.wfs.query;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface OdsQueryFilter {

    public String getQueryParameterName();

    public Element buildXmlElement(Document doc);

}