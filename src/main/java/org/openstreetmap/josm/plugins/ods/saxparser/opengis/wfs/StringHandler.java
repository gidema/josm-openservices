package org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.saxparser.api.AbstractSaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxElementHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class StringHandler extends AbstractSaxElementHandler {
    private final StringBuilder sb = new StringBuilder();
    
    public StringHandler(SaxElementHandler parentHandler) {
        super(parentHandler);
    }

    @Override
    public void start(Attributes atts) {
        sb.setLength(0);
    }

    @Override
    public SaxElementHandler startElement(QName qName, Attributes atts) throws SAXException {
        throw unexpectedElement(qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        sb.append(ch, start, length);
    }

    @Override
    public void endElement(QName qName) throws SAXException {
        throw unexpectedElement(qName);
    }

    @Override
    public void end() {
        // No action required
    }
    
    public String getValue() {
        return sb.length() == 0 ? null : sb.toString();
    }

}
