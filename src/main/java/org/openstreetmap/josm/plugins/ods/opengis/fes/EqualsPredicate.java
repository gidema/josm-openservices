package org.openstreetmap.josm.plugins.ods.opengis.fes;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EqualsPredicate implements FilterPredicate {
    private QName property;
    private String value;
    
    public EqualsPredicate(QName property, String value) {
        super();
        this.property = property;
        this.value = value;
    }

    @Override
    public Element buildXmlElement(Document doc) {
        Element equals = doc.createElementNS(Fes.NS_FES_20, "PropertyIsEqualTo");
        Element reference = doc.createElementNS(Fes.NS_FES_20, "ValueReference");
        reference.appendChild(doc.createTextNode(property.getLocalPart()));
        Element literal =  doc.createElementNS(Fes.NS_FES_20, "Literal");
        literal.appendChild(doc.createTextNode(value));
        equals.appendChild(reference);
        equals.appendChild(literal);
        return equals;
    }
}
