package org.openstreetmap.josm.plugins.ods.opengis.fes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface FilterPredicate {
    public Element buildXmlElement(Document doc);
}
